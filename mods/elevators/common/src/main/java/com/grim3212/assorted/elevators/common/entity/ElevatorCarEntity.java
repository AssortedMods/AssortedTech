package com.grim3212.assorted.elevators.common.entity;

import com.grim3212.assorted.elevators.api.util.ElevatorsDamageTypes;
import com.grim3212.assorted.elevators.common.block.CamouflagedElevatorBlock;
import com.grim3212.assorted.elevators.common.block.ElevatorBlock;
import com.grim3212.assorted.elevators.common.block.ElevatorGroup;
import com.grim3212.assorted.elevators.common.block.ElevatorShaft;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.blockentity.CamouflageBlockEntity;
import com.grim3212.assorted.elevators.mixin.ServerGamePacketListenerImplAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * An elevator car on its way along the shaft, carrying whatever stands on it and crushing whatever
 * is under it, which turns back into its blocks when it arrives. Both sides move it from the synced trip.
 */
public class ElevatorCarEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_START_Y = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_Y = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPEED = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SIZE_X = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SIZE_Z = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.INT);
    /** Snaps a client that is running behind to the end before the blocks replace the car. */
    private static final EntityDataAccessor<Boolean> DATA_ARRIVED = SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.BOOLEAN);
    /**
     * Each cell's block, indexed by {@link #cellIndex}: empty for a plain elevator, else a camouflaged
     * one wearing the state held, or its own state for no disguise; air would sync as empty.
     */
    private static final List<EntityDataAccessor<Optional<BlockState>>> DATA_CELLS = IntStream.range(0, ElevatorGroup.MAX_SIZE * ElevatorGroup.MAX_SIZE)
            .mapToObj(i -> SynchedEntityData.defineId(ElevatorCarEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE)).toList();

    /** Blocks per tick squared: about two blocks to reach full speed, and two more to stop. */
    private static final double ACCELERATION = 0.01D;
    /** What it starts off and creeps in at, so it is never stuck at no speed at all. */
    private static final double MIN_SPEED = 0.03D;
    private static final float CRUSH_DAMAGE = 4.0F;
    /** How long it rests at the end as an entity, so riders are settled on it before it turns solid. */
    private static final int SETTLE_TICKS = 4;
    /** Feet this far into the car still get lifted onto it, so something falling onto a rising car is caught. */
    private static final double CARRY_BELOW = 0.5D;
    /** Feet this far above a descending car are still standing on it, not jumping. */
    private static final double CARRY_ABOVE = 0.05D;

    private int settledTicks;
    private boolean landed;
    /** Vanilla light blocks it keeps in the cells its glowing disguises are passing through, as no entity can give off light. */
    private final Set<BlockPos> lights = new HashSet<>();

    public ElevatorCarEntity(EntityType<? extends ElevatorCarEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    /** {@code cells} as {@link #DATA_CELLS} holds them. */
    public ElevatorCarEntity(Level level, ElevatorGroup group, int startY, int targetY, float speed, List<Optional<BlockState>> cells) {
        this(ElevatorsEntities.ELEVATOR_CAR.get(), level);
        for (int i = 0; i < DATA_CELLS.size(); i++) {
            this.entityData.set(DATA_CELLS.get(i), cells.get(i));
        }
        this.entityData.set(DATA_SIZE_X, group.sizeX());
        this.entityData.set(DATA_SIZE_Z, group.sizeZ());
        this.entityData.set(DATA_START_Y, startY);
        this.entityData.set(DATA_TARGET_Y, targetY);
        this.entityData.set(DATA_SPEED, speed);
        this.setPos(group.centerX(), startY, group.centerZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_START_Y, 0);
        builder.define(DATA_TARGET_Y, 0);
        builder.define(DATA_SPEED, 0.2F);
        builder.define(DATA_SIZE_X, 1);
        builder.define(DATA_SIZE_Z, 1);
        builder.define(DATA_ARRIVED, false);
        DATA_CELLS.forEach(cell -> builder.define(cell, Optional.empty()));
    }

    public int getTargetY() {
        return this.entityData.get(DATA_TARGET_Y);
    }

    public int getSizeX() {
        return this.entityData.get(DATA_SIZE_X);
    }

    public int getSizeZ() {
        return this.entityData.get(DATA_SIZE_Z);
    }

    public ElevatorGroup getGroup() {
        return new ElevatorGroup(Mth.floor(this.getX() - this.getSizeX() / 2.0D + 0.5D), Mth.floor(this.getZ() - this.getSizeZ() / 2.0D + 0.5D), this.getSizeX(), this.getSizeZ());
    }

    public static int cellIndex(int x, int z) {
        return x * ElevatorGroup.MAX_SIZE + z;
    }

    /** The block the cell at {@code x}, {@code z} of the footprint turns back into. */
    public BlockState getCellBlock(int x, int z) {
        Block block = this.entityData.get(DATA_CELLS.get(cellIndex(x, z))).isPresent() ? ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get() : ElevatorsBlocks.ELEVATOR.get();
        return block.defaultBlockState().setValue(ElevatorBlock.GOING_UP, this.getTargetY() >= this.entityData.get(DATA_START_Y));
    }

    private BlockState getCellDisguise(int x, int z) {
        BlockState held = this.entityData.get(DATA_CELLS.get(cellIndex(x, z))).orElse(Blocks.AIR.defaultBlockState());
        return held.getBlock() instanceof CamouflagedElevatorBlock ? Blocks.AIR.defaultBlockState() : held;
    }

    /** The value {@link #DATA_CELLS} holds for a camouflaged elevator wearing {@code disguise}, air for none. */
    public static Optional<BlockState> camouflagedCell(BlockState disguise) {
        return Optional.of(disguise.isAir() ? ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get().defaultBlockState() : disguise);
    }

    /** What the cell looks like on the way: its disguise if it wears one. */
    public BlockState getCellLook(int x, int z) {
        BlockState disguise = this.getCellDisguise(x, z);
        return disguise.isAir() ? this.getCellBlock(x, z) : disguise;
    }

    @Override
    protected AABB makeBoundingBox() {
        Vec3 position = this.position();
        double halfX = this.getSizeX() / 2.0D;
        double halfZ = this.getSizeZ() / 2.0D;
        return new AABB(position.x - halfX, position.y, position.z - halfZ, position.x + halfX, position.y + 1.0D, position.z + halfZ);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.landed) {
            // A tick after the blocks, so a client always gets the blocks before it loses the car.
            this.discard();
            return;
        }

        double dy = this.step();
        if (!this.level().isClientSide() && dy != 0.0D) {
            int blocked = this.blockedLayer(dy);
            if (blocked != Integer.MIN_VALUE) {
                // Something was built into the shaft: stop as close to it as the car and its rider fit.
                this.entityData.set(DATA_TARGET_Y, dy > 0.0D ? blocked - 1 - ElevatorShaft.HEADROOM : blocked + 1);
                dy = this.step();
            }
        }

        if (dy != 0.0D) {
            this.moveCar(dy);
            if (this.level() instanceof ServerLevel level) {
                this.updateLights(level);
            }
        } else if (!this.level().isClientSide()) {
            this.entityData.set(DATA_ARRIVED, true);
            this.holdRiders();
            this.keepRidersGrounded();
            if (++this.settledTicks >= SETTLE_TICKS) {
                this.arrive();
            }
        }
    }

    /** This tick's move: speeding up away from the start and slowing down into the stop, from nothing but its height, so both sides agree. */
    private double step() {
        double remaining = this.getTargetY() - this.getY();
        if (remaining == 0.0D) {
            return 0.0D;
        }

        double travelled = Math.abs(this.getY() - this.entityData.get(DATA_START_Y));
        double speed = Math.min(this.entityData.get(DATA_SPEED), MIN_SPEED + Math.sqrt(2.0D * ACCELERATION * Math.min(travelled, Math.abs(remaining))));
        return Math.signum(remaining) * Math.min(speed, Math.abs(remaining));
    }

    /** The layer the next step runs the car, or going up its rider's head, into if it is solid; {@link Integer#MIN_VALUE} if clear. */
    private int blockedLayer(double dy) {
        double next = this.getY() + dy;
        int y = dy > 0.0D ? Mth.floor(next + 1.0D + ElevatorShaft.HEADROOM - 1.0E-5D) : Mth.floor(next);
        return ElevatorShaft.isShaft(this.level(), this.getGroup(), y) ? Integer.MIN_VALUE : y;
    }

    private void moveCar(double dy) {
        AABB before = this.getBoundingBox();
        double oldTop = before.maxY;
        double newTop = oldTop + dy;
        this.setPos(this.getX(), this.getY() + dy, this.getZ());

        double highestFeet = Math.max(newTop, oldTop + CARRY_ABOVE);
        AABB zone = new AABB(before.minX, oldTop - CARRY_BELOW, before.minZ, before.maxX, highestFeet + 1.0D, before.maxZ);
        for (Entity entity : this.level().getEntities(this, zone, ElevatorCarEntity::isCarried)) {
            if (entity instanceof ServerPlayer player) {
                // The rider's own client moves them; the server only has to stop calling it flying.
                resetFlyingTicks(player);
                player.resetFallDistance();
            }

            double feet = entity.getY();
            if (!movedHere(entity) || feet < oldTop - CARRY_BELOW || feet > highestFeet) {
                continue;
            }

            entity.setPos(entity.getX(), newTop, entity.getZ());
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < 0.0D) {
                entity.setDeltaMovement(motion.x, 0.0D, motion.z);
            }
            entity.resetFallDistance();
        }

        if (dy < 0.0D && this.level() instanceof ServerLevel level) {
            this.crush(level, newTop);
        }
    }

    /** Hurts whatever the descending car comes down on; it keeps going regardless. */
    private void crush(ServerLevel level, double top) {
        DamageSource source = ElevatorsDamageTypes.source(level, ElevatorsDamageTypes.ELEVATOR, this, null);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), entity -> entity.getY() < top - CARRY_BELOW && !entity.isSpectator())) {
            entity.hurt(source, CRUSH_DAMAGE);
        }
    }

    /**
     * Moves each glowing cell's light block to the cell it is mostly in now. A cell's look is placed
     * nowhere, so its own state's emission is the one to ask; NeoForge deprecates that for its
     * level-aware overload, which only exists in its patched jar.
     */
    @SuppressWarnings("deprecation")
    private void updateLights(ServerLevel level) {
        Set<BlockPos> wanted = new HashSet<>();
        int y = Mth.floor(this.getY() + 0.5D);
        ElevatorGroup group = this.getGroup();
        for (int x = 0; x < group.sizeX(); x++) {
            for (int z = 0; z < group.sizeZ(); z++) {
                int emission = this.getCellLook(x, z).getLightEmission();
                BlockPos pos = new BlockPos(group.minX() + x, y, group.minZ() + z);
                BlockState there = level.getBlockState(pos);
                boolean water = there.getFluidState().is(Fluids.WATER) && there.getFluidState().isSource();
                // Only air, still water or a light of its own, never a light block someone placed.
                if (emission <= 0 || !(there.isAir() || water || there.is(Blocks.LIGHT) && this.lights.contains(pos))) {
                    continue;
                }

                wanted.add(pos);
                BlockState light = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, emission).setValue(LightBlock.WATERLOGGED, water);
                if (there != light) {
                    level.setBlock(pos, light, Block.UPDATE_ALL);
                }
            }
        }

        for (BlockPos old : this.lights) {
            if (!wanted.contains(old)) {
                clearLight(level, old);
            }
        }
        this.lights.clear();
        this.lights.addAll(wanted);
    }

    private void clearLights() {
        this.lights.forEach(pos -> clearLight(this.level(), pos));
        this.lights.clear();
    }

    private static void clearLight(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.LIGHT)) {
            level.setBlock(pos, state.getValue(LightBlock.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    /**
     * Keeps what the server moves standing on the car while it rests at the end. The car is not
     * solid on the server (see {@link #canBeCollidedWith()}), so without this a mob riding it would
     * start to fall through it before it turns back into blocks.
     */
    private void holdRiders() {
        AABB top = this.getBoundingBox();
        double surface = top.maxY;
        for (Entity entity : this.level().getEntities(this, new AABB(top.minX, surface - CARRY_BELOW, top.minZ, top.maxX, surface + CARRY_ABOVE, top.maxZ), ElevatorCarEntity::isCarried)) {
            if (!movedHere(entity) || entity.getY() < surface - CARRY_BELOW || entity.getY() > surface + CARRY_ABOVE) {
                continue;
            }

            entity.setPos(entity.getX(), surface, entity.getZ());
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < 0.0D) {
                entity.setDeltaMovement(motion.x, 0.0D, motion.z);
            }
            entity.resetFallDistance();
        }
    }

    /** Resting at the end still counts as standing on something to the server. */
    private void keepRidersGrounded() {
        AABB top = this.getBoundingBox();
        for (ServerPlayer player : this.level().getEntitiesOfClass(ServerPlayer.class, new AABB(top.minX, top.maxY - CARRY_BELOW, top.minZ, top.maxX, top.maxY + 1.0D, top.maxZ))) {
            resetFlyingTicks(player);
        }
    }

    /**
     * Stops the server counting a rider as floating, which it would otherwise kick them for once a
     * trip runs past four seconds where flight is not allowed. The car is no block under their feet.
     */
    private static void resetFlyingTicks(ServerPlayer player) {
        ((ServerGamePacketListenerImplAccessor) player.connection).assortedelevators_setAboveGroundTickCount(0);
    }

    /**
     * Whether this side moves {@code entity}: a player's own client moves them, and the server moves
     * everything else.
     */
    private static boolean movedHere(Entity entity) {
        return entity instanceof Player player ? player.isLocalPlayer() : entity.isControlledByLocalInstance();
    }

    private static boolean isCarried(Entity entity) {
        return !(entity instanceof ElevatorCarEntity) && !entity.isPassenger() && !entity.noPhysics && !entity.isSpectator() && !(entity instanceof Player player && player.getAbilities().flying);
    }

    /** Becomes its blocks again, dropping any whose place was built over meanwhile. */
    private void arrive() {
        Level level = this.level();
        int y = Mth.floor(this.getY() + 0.5D);
        ElevatorGroup group = this.getGroup();
        for (BlockPos cell : group.cells(y)) {
            int x = cell.getX() - group.minX();
            int z = cell.getZ() - group.minZ();
            BlockState block = this.getCellBlock(x, z);
            if (!ElevatorShaft.isShaft(level, cell)) {
                if (level instanceof ServerLevel) {
                    this.spawnAtLocation(block.getBlock());
                }
                continue;
            }

            level.setBlock(cell, block.setValue(ElevatorBlock.POWERED, level.hasNeighborSignal(cell)), Block.UPDATE_ALL);
            BlockState disguise = this.getCellDisguise(x, z);
            if (!disguise.isAir() && level.getBlockEntity(cell) instanceof CamouflageBlockEntity camouflage) {
                camouflage.setDisguise(disguise);
            }
        }
        // Most were just built over by the blocks themselves; this is any left over.
        this.clearLights();
        ElevatorShaft.updateLandings(level, group, y);
        level.playSound(null, BlockPos.containing(group.centerX(), y, group.centerZ()), SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.5F, 1.2F);
        this.landed = true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SIZE_X.equals(key) || DATA_SIZE_Z.equals(key)) {
            this.setBoundingBox(this.makeBoundingBox());
        } else if (DATA_ARRIVED.equals(key) && this.level().isClientSide() && this.entityData.get(DATA_ARRIVED) && this.getY() != this.getTargetY()) {
            this.moveCar(this.getTargetY() - this.getY());
        }
    }

    /**
     * Each client runs the car from its trip; the server's position updates would only drag it back
     * by the ping. A client skips the move packets of an entity it controls.
     */
    @Override
    public boolean isControlledByLocalInstance() {
        return true;
    }

    /**
     * Solid only on a client, where a player is stood on it by their own client; a solid car on the
     * server would argue with that. What the server moves is held on it by {@link #holdRiders()}.
     */
    @Override
    public boolean canBeCollidedWith() {
        return this.level().isClientSide();
    }

    /** Killed or gone for good, it takes its lights with it; only a chunk unloading keeps them, saved beside it. */
    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && reason != RemovalReason.UNLOADED_TO_CHUNK && reason != RemovalReason.UNLOADED_WITH_PLAYER) {
            this.clearLights();
        }
        super.remove(reason);
    }

    /** Once landed its blocks are already placed; saving it too would land it twice. */
    @Override
    public boolean shouldBeSaved() {
        return !this.landed && super.shouldBeSaved();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(DATA_SIZE_X, Mth.clamp(tag.contains("SizeX", Tag.TAG_INT) ? tag.getInt("SizeX") : 1, 1, ElevatorGroup.MAX_SIZE));
        this.entityData.set(DATA_SIZE_Z, Mth.clamp(tag.contains("SizeZ", Tag.TAG_INT) ? tag.getInt("SizeZ") : 1, 1, ElevatorGroup.MAX_SIZE));
        this.entityData.set(DATA_START_Y, tag.contains("StartY", Tag.TAG_INT) ? tag.getInt("StartY") : Mth.floor(this.getY()));
        this.entityData.set(DATA_TARGET_Y, tag.contains("TargetY", Tag.TAG_INT) ? tag.getInt("TargetY") : Mth.floor(this.getY()));
        this.entityData.set(DATA_SPEED, tag.contains("Speed", Tag.TAG_FLOAT) ? tag.getFloat("Speed") : 0.2F);
        for (int i = 0; i < DATA_CELLS.size(); i++) {
            String key = "Cell" + i;
            this.entityData.set(DATA_CELLS.get(i), tag.contains(key, Tag.TAG_COMPOUND)
                    ? Optional.of(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound(key)))
                    : Optional.empty());
        }
        this.lights.clear();
        if (tag.contains("Lights")) {
            this.lights.addAll(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("Lights")).result().orElse(List.of()));
        }
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        for (int i = 0; i < DATA_CELLS.size(); i++) {
            Optional<BlockState> cell = this.entityData.get(DATA_CELLS.get(i));
            if (cell.isPresent()) {
                tag.put("Cell" + i, NbtUtils.writeBlockState(cell.get()));
            }
        }
        BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, List.copyOf(this.lights)).result().ifPresent(lights -> tag.put("Lights", lights));
        tag.putInt("SizeX", this.getSizeX());
        tag.putInt("SizeZ", this.getSizeZ());
        tag.putInt("StartY", this.entityData.get(DATA_START_Y));
        tag.putInt("TargetY", this.getTargetY());
        tag.putFloat("Speed", this.entityData.get(DATA_SPEED));
    }
}
