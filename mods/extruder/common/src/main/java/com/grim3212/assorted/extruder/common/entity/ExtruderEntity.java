package com.grim3212.assorted.extruder.common.entity;

import com.grim3212.assorted.lib.core.inventory.IMenuDataProvider;
import com.grim3212.assorted.lib.dist.Dist;
import com.grim3212.assorted.lib.dist.DistExecutor;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.extruder.ExtruderCommonMod;
import com.grim3212.assorted.extruder.api.ExtruderTags;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.client.ExtruderClientHandlers;
import com.grim3212.assorted.extruder.common.inventory.ExtruderMenu;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * A fueled drill that travels in a straight line, mining what is in front of it into its own
 * inventory and laying the blocks it carries behind itself, one slot after the next. Punching it
 * breaks it like a boat, and its screen sets its direction and starts and stops it.
 * <p>
 * Its {@link ExtruderType} decides what it can mine, by the tools it was made from, and how many of
 * its slots it uses. The container always has room for the largest, so a slot is the same index
 * whatever the material; a smaller extruder just leaves the rest empty and out of its screen.
 * <p>
 * It is a plain {@link Entity} carrying a vehicle's hurt time, hurt direction and damage itself:
 * 1.21.1's {@code VehicleEntity} keeps {@code getDropItem} package private on Fabric, so nothing
 * outside vanilla's package can extend it there.
 */
public class ExtruderEntity extends Entity implements IMenuDataProvider<ExtruderType> {

    public static final int FUEL_SLOT = 0;
    public static final int FIRST_EXTRUDE_SLOT = 1;
    public static final int MAX_EXTRUDE_SLOTS = 9;
    public static final int FIRST_MINED_SLOT = FIRST_EXTRUDE_SLOT + MAX_EXTRUDE_SLOTS;
    public static final int MAX_MINED_SLOTS = 27;
    public static final int SLOTS = FIRST_MINED_SLOT + MAX_MINED_SLOTS;

    /** Menu buttons 0 to 5 are {@link Direction#get3DDataValue()}; this one starts and stops it. */
    public static final int TOGGLE_BUTTON = 6;

    private static final EntityDataAccessor<Integer> DATA_HURT_TIME = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HURT_DIR = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Direction> DATA_FACING = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Boolean> DATA_RUNNING = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_FUEL = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.INT);
    /** The {@link ExtruderType}'s ordinal, which the renderer needs for its look. */
    private static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(ExtruderEntity.class, EntityDataSerializers.INT);

    private final SimpleContainer container = new SimpleContainer(SLOTS);
    /** How far ahead of its centre it looks for the block to mine. */
    private static final double FRONT_REACH = 0.51D;

    /** How soon a second empty handed creative punch has to follow the first to break it: half a second. */
    private static final int DOUBLE_PUNCH_TICKS = 10;

    private int lastCreativePunch = -DOUBLE_PUNCH_TICKS - 1;
    /** The extrusion slot the next block is laid from; it walks round all of its own, empty or not. */
    private int contentHead = FIRST_EXTRUDE_SLOT;
    private @Nullable BlockPos lastBlock;
    /** The block just left, laid into once the extruder's box is clear of it so it cannot be trapped. */
    private @Nullable BlockPos pendingExtrude;

    /** The position the server last sent, which the client moves towards over a few ticks, as a boat does. */
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ExtruderEntity.this.getFuel();
                case 1 -> ExtruderEntity.this.isRunning() ? 1 : 0;
                case 2 -> ExtruderEntity.this.getFacing().get3DDataValue();
                case 3 -> fuelValue(ExtruderEntity.this.container.getItem(FUEL_SLOT));
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return ExtruderMenu.DATA_COUNT;
        }
    };

    public ExtruderEntity(EntityType<? extends ExtruderEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HURT_TIME, 0);
        builder.define(DATA_HURT_DIR, 1);
        builder.define(DATA_DAMAGE, 0.0F);
        builder.define(DATA_FACING, Direction.NORTH);
        builder.define(DATA_RUNNING, false);
        builder.define(DATA_FUEL, 0);
        builder.define(DATA_TYPE, ExtruderType.IRON.ordinal());
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_HURT_TIME);
    }

    public void setHurtTime(int hurtTime) {
        this.entityData.set(DATA_HURT_TIME, hurtTime);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_HURT_DIR);
    }

    public void setHurtDir(int hurtDir) {
        this.entityData.set(DATA_HURT_DIR, hurtDir);
    }

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DATA_DAMAGE, damage);
    }

    public ExtruderType getExtruderType() {
        return ExtruderType.byId(this.entityData.get(DATA_TYPE));
    }

    public void setExtruderType(ExtruderType type) {
        this.entityData.set(DATA_TYPE, type.ordinal());
    }

    public Direction getFacing() {
        return this.entityData.get(DATA_FACING);
    }

    public void setFacing(Direction facing) {
        this.entityData.set(DATA_FACING, facing);
    }

    public boolean isRunning() {
        return this.entityData.get(DATA_RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(DATA_RUNNING, running);
        if (!running) {
            this.snapToBlock();
        }
    }

    public int getFuel() {
        return this.entityData.get(DATA_FUEL);
    }

    private void setFuel(int fuel) {
        this.entityData.set(DATA_FUEL, Math.max(0, fuel));
    }

    public SimpleContainer getContainer() {
        return this.container;
    }

    /** The block the extruder is in, by the middle of its box rather than its feet. */
    public BlockPos currentBlock() {
        return BlockPos.containing(this.getBoundingBox().getCenter());
    }

    /** Puts the extruder in the middle of {@code pos}, which is how it is placed and where it stops. */
    public void placeAt(BlockPos pos) {
        this.moveTo(pos.getX() + 0.5D, pos.getY() + (1.0D - this.getBbHeight()) / 2.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        this.lastBlock = pos.immutable();
        this.pendingExtrude = null;
    }

    private void snapToBlock() {
        this.placeAt(this.currentBlock());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.level() instanceof ServerLevel level) {
            if (this.isRunning()) {
                this.run(level);
            }
        } else {
            this.tickLerp();
        }
    }

    /**
     * Without this, the client puts it wherever each position update says the moment it arrives,
     * out of step with its own ticks, so a moving extruder stutters. Boats and minecarts do the same.
     */
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpSteps = steps;
    }

    @Override
    public double lerpTargetX() {
        return this.lerpSteps > 0 ? this.lerpX : this.getX();
    }

    @Override
    public double lerpTargetY() {
        return this.lerpSteps > 0 ? this.lerpY : this.getY();
    }

    @Override
    public double lerpTargetZ() {
        return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
    }

    private void tickLerp() {
        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.getYRot(), this.getXRot());
            this.lerpSteps--;
        }
    }

    private void run(ServerLevel level) {
        if (!this.refuelIfOut(level)) {
            return;
        }

        Direction facing = this.getFacing();
        Vec3 front = this.getBoundingBox().getCenter().add(Vec3.atLowerCornerOf(facing.getNormal()).scale(FRONT_REACH));
        BlockPos ahead = BlockPos.containing(front);
        BlockState state = level.getBlockState(ahead);

        // Fluids are travelled through; anything else in the way is mined, or stops it, as its Stop
        // button would, rather than leaving it running against a block it will never get through.
        // A fluid here is a liquid block or a bubble column, the blocks vanilla flags as liquid.
        if (!state.isAir() && !(state.getBlock() instanceof LiquidBlock) && !(state.getBlock() instanceof BubbleColumnBlock)) {
            ItemStack tool = this.getExtruderType().toolFor(state);
            if (!canMine(level, ahead, state) || tool.isEmpty()) {
                this.setRunning(false);
                return;
            }
            this.mine(level, ahead, state, tool);
        }

        this.setFuel(this.getFuel() - 1);
        double speed = ExtruderCommonMod.COMMON_CONFIG.extruderMoveSpeed.get() * this.getExtruderType().getSpeed();
        this.move(MoverType.SELF, Vec3.atLowerCornerOf(facing.getNormal()).scale(speed));

        BlockPos current = this.currentBlock();
        if (this.lastBlock == null) {
            this.lastBlock = current;
        } else if (!current.equals(this.lastBlock)) {
            this.pendingExtrude = this.lastBlock;
            this.lastBlock = current;
        }

        if (this.pendingExtrude != null && !this.getBoundingBox().intersects(new AABB(this.pendingExtrude))) {
            this.extrude(level, this.pendingExtrude);
            this.pendingExtrude = null;
        }

        // Burning the next fuel now rather than at the start of the next tick changes nothing about
        // what it spends, but it stops the tick it runs dry rather than a tick later.
        this.refuelIfOut(level);
    }

    /** Burns the next fuel once it is out; with nothing left to burn it stops, as its Stop button does. */
    private boolean refuelIfOut(ServerLevel level) {
        if (this.getFuel() <= 0) {
            this.refuel(level);
            if (this.getFuel() <= 0) {
                this.setRunning(false);
                return false;
            }
        }
        return true;
    }

    public static boolean canMine(Level level, BlockPos pos, BlockState state) {
        return state.getDestroySpeed(level, pos) >= 0.0F && !state.is(ExtruderTags.Blocks.EXTRUDER_UNMINEABLE);
    }

    /**
     * Mines with whichever of its tools suits the block, as a player would, into the mined slots;
     * what does not fit is dropped where the extruder is.
     */
    private void mine(ServerLevel level, BlockPos pos, BlockState state, ItemStack tool) {
        for (ItemStack drop : Block.getDrops(state, level, pos, level.getBlockEntity(pos), this, tool)) {
            ItemStack left = this.addToMined(drop);
            if (!left.isEmpty()) {
                this.spawnAtLocation(left);
            }
        }

        level.levelEvent(2001, pos, Block.getId(state));
        level.removeBlock(pos, false);
        this.setFuel(this.getFuel() - this.getExtruderType().blockCost(ExtruderCommonMod.COMMON_CONFIG.extruderFuelPerMinedBlock.get()));
    }

    private ItemStack addToMined(ItemStack stack) {
        ItemStack left = stack.copy();
        for (int slot = FIRST_MINED_SLOT; slot < FIRST_MINED_SLOT + this.getExtruderType().getMinedSlots() && !left.isEmpty(); slot++) {
            ItemStack in = this.container.getItem(slot);
            if (in.isEmpty()) {
                this.container.setItem(slot, left);
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(in, left)) {
                int moved = Math.min(left.getCount(), in.getMaxStackSize() - in.getCount());
                in.grow(moved);
                left.shrink(moved);
            }
        }
        return left;
    }

    /** Lays the next extrusion slot's block where the extruder just was, if it has one to lay. */
    private void extrude(ServerLevel level, BlockPos pos) {
        int slot = this.contentHead;
        this.contentHead = slot >= FIRST_EXTRUDE_SLOT + this.getExtruderType().getExtrudeSlots() - 1 ? FIRST_EXTRUDE_SLOT : slot + 1;

        ItemStack stack = this.container.getItem(slot);
        if (!(stack.getItem() instanceof BlockItem blockItem) || !level.getBlockState(pos).canBeReplaced()) {
            return;
        }

        BlockState state = blockItem.getBlock().defaultBlockState();
        if (!state.canSurvive(level, pos) || !level.setBlockAndUpdate(pos, state)) {
            return;
        }

        stack.shrink(1);
        this.setFuel(this.getFuel() - this.getExtruderType().blockCost(ExtruderCommonMod.COMMON_CONFIG.extruderFuelPerExtrudedBlock.get()));
        SoundType sound = Services.LEVEL_PROPERTIES.getSoundType(level, pos, this);
        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }

    private void refuel(ServerLevel level) {
        ItemStack stack = this.container.getItem(FUEL_SLOT);
        int burn = fuelValue(stack);
        if (burn <= 0) {
            return;
        }

        this.setFuel(this.getFuel() + burn);
        if (stack.is(Items.LAVA_BUCKET)) {
            this.container.setItem(FUEL_SLOT, new ItemStack(Items.BUCKET));
        } else {
            stack.shrink(1);
        }
    }

    /** Anything a furnace burns, for as long as a furnace would. */
    public static int fuelValue(ItemStack stack) {
        return Services.PLATFORM.getFuelTime(stack, null);
    }

    /**
     * How many blocks an extruder of {@code speed} gets through, from the middle of a block, on the
     * fuel burning now and then {@code items} more of {@code itemFuel} each, burnt one at a time, if
     * every block costs {@code costPerBlock}: mined as its front enters it, or laid once its box has
     * cleared it.
     */
    public static int blocksFor(int fuel, int itemFuel, int items, float speed, int costPerBlock, boolean laying) {
        double perTick = ExtruderCommonMod.COMMON_CONFIG.extruderMoveSpeed.get() * speed;
        if (perTick <= 0.0D) {
            return 0;
        }
        // Along its facing, with the block it starts in spanning 0 to 1 and its centre at 0.5.
        double centre = 0.5D;
        double nextBlock = laying ? 1.0D + ExtruderEntities.EXTRUDER.get().getWidth() / 2.0D : 1.0D - FRONT_REACH;
        int blocks = 0;

        while (true) {
            if (fuel <= 0) {
                if (items <= 0 || itemFuel <= 0) {
                    return blocks;
                }
                fuel = itemFuel;
                items--;
            }

            if (!laying && centre >= nextBlock) {
                fuel = Math.max(0, fuel - costPerBlock);
                blocks++;
                nextBlock += 1.0D;
            }
            fuel = Math.max(0, fuel - 1);
            centre += perTick;
            if (laying && centre >= nextBlock) {
                fuel = Math.max(0, fuel - costPerBlock);
                blocks++;
                nextBlock += 1.0D;
            }

            // The ticks before the next block only travel: each needs fuel left at its start, and
            // stops short of the tick that reaches the block.
            long untilBlock = (long) Math.ceil((nextBlock - centre) / perTick) - (laying ? 1 : 0);
            long travel = Math.min(fuel, Math.max(0L, untilBlock));
            fuel -= (int) travel;
            centre += travel * perTick;
        }
    }

    /** A button on its screen: a direction to face, or start and stop. */
    public void handleButton(int id) {
        if (id == TOGGLE_BUTTON) {
            this.setRunning(!this.isRunning());
        } else if (id >= 0 && id < Direction.values().length) {
            this.setFacing(Direction.from3DDataValue(id));
        }
    }

    /**
     * As in GrimPack, punching it empty handed starts or stops it, and does it no harm. Punching it
     * sneaking or holding anything, or any other damage, wears it down until it breaks, as a
     * minecart does. A creative player breaks it at once that way, or with a second empty handed
     * punch straight after the first.
     */
    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (!(this.level() instanceof ServerLevel level) || this.isRemoved()) {
            return true;
        }
        if (source.getDirectEntity() instanceof ServerPlayer player && source.getEntity() == player
                && player.getMainHandItem().isEmpty() && !player.isSecondaryUseActive()) {
            if (player.getAbilities().instabuild) {
                boolean second = this.tickCount - this.lastCreativePunch <= DOUBLE_PUNCH_TICKS;
                this.lastCreativePunch = this.tickCount;
                if (second) {
                    this.breakInCreative();
                    return true;
                }
            }
            // With nothing to burn it would only stop itself again on its next tick.
            boolean fuelless = this.getFuel() <= 0 && fuelValue(this.container.getItem(FUEL_SLOT)) <= 0;
            this.setRunning(!this.isRunning() && !fuelless);
            String key = this.isRunning() ? "message.extruder.started" : fuelless ? "message.extruder.no_fuel" : "message.extruder.stopped";
            player.displayClientMessage(Component.translatable(key), true);
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            return true;
        }
        if (source.getEntity() instanceof Player player && player.getAbilities().instabuild) {
            this.breakInCreative();
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        // As a boat or minecart wears down: it breaks into its item once the damage passes 40.
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + damage * 10.0F);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        if (this.getDamage() > 40.0F) {
            this.destroy(level);
        }
        return true;
    }

    /** Killed, dropping its item with any name it was given; what it holds spills from {@link #remove}. */
    private void destroy(ServerLevel level) {
        this.kill();
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = new ItemStack(this.getDropItem());
            stack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
            this.spawnAtLocation(stack);
        }
    }

    /**
     * Vanilla's creative break discards the vehicle, which would lose everything the extruder holds;
     * this spills it as a survival break does, without the extruder itself.
     */
    private void breakInCreative() {
        Containers.dropContents(this.level(), this, this.container);
        this.breakParticles();
        this.discard();
    }

    /** Bits of the extruder itself flying off it, as a tool's do when it breaks. */
    private void breakParticles() {
        if (this.level() instanceof ServerLevel level) {
            Vec3 centre = this.getBoundingBox().getCenter();
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.getDropItem())), centre.x, centre.y, centre.z, 24, 0.25D, 0.25D, 0.25D, 0.08D);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            Services.PLATFORM.openMenu(serverPlayer, this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    /** The client's menu is built for its material, which sets how many slots it shows. */
    @Override
    public ExtruderType getMenuData(ServerPlayer player) {
        return this.getExtruderType();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ExtruderMenu(id, inventory, this.container, this.data, this.getExtruderType(), this);
    }

    /** Named for its material, as its item is. */
    @Override
    protected Component getTypeName() {
        return new ItemStack(this.getDropItem()).getHoverName();
    }

    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    private Item getDropItem() {
        return ExtruderItems.extruder(this.getExtruderType());
    }

    /** A held punch that broke it would otherwise carry on into the block behind it. */
    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ExtruderClientHandlers.releaseAttackIfPunching(this));
    }

    /** Everything it holds spills out when it is broken, the way a chest boat's does, and bits of it fly off. */
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide() && reason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this.container);
            this.breakParticles();
        }
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setExtruderType(ExtruderType.byName(tag.getString("Type"), ExtruderType.IRON));
        Direction facing = Direction.byName(tag.getString("Facing"));
        this.setFacing(facing != null ? facing : Direction.NORTH);
        this.entityData.set(DATA_RUNNING, tag.getBoolean("Running"));
        this.setFuel(tag.getInt("Fuel"));
        int head = tag.contains("ContentHead") ? tag.getInt("ContentHead") : FIRST_EXTRUDE_SLOT;
        this.contentHead = Math.clamp(head, FIRST_EXTRUDE_SLOT, FIRST_EXTRUDE_SLOT + this.getExtruderType().getExtrudeSlots() - 1);
        this.container.clearContent();
        ContainerHelper.loadAllItems(tag, this.container.getItems(), this.registryAccess());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Type", this.getExtruderType().getSerializedName());
        tag.putString("Facing", this.getFacing().getSerializedName());
        tag.putBoolean("Running", this.isRunning());
        tag.putInt("Fuel", this.getFuel());
        tag.putInt("ContentHead", this.contentHead);
        ContainerHelper.saveAllItems(tag, this.container.getItems(), this.registryAccess());
    }
}
