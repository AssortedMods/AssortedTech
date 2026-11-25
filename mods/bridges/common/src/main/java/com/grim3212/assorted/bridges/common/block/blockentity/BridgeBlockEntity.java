package com.grim3212.assorted.bridges.common.block.blockentity;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.bridges.BridgesCommonMod;
import com.grim3212.assorted.bridges.common.block.BridgeBlock;
import com.grim3212.assorted.bridges.common.block.BridgeControlBlock;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.bridges.common.properties.BridgesModelProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BridgeBlockEntity extends BlockEntity implements IBlockEntityWithModelData {

    protected BlockState blockState = Blocks.AIR.defaultBlockState();
    protected Direction facing = Direction.NORTH;

    public BridgeBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public BridgeBlockEntity(BlockPos pos, BlockState state) {
        super(BridgesBlockEntityTypes.BRIDGE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.blockState = tag.contains("stored_state", Tag.TAG_COMPOUND)
                ? NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("stored_state"))
                : Blocks.AIR.defaultBlockState();
        this.facing = Direction.from3DDataValue(tag.contains("facing", Tag.TAG_INT) ? tag.getInt("facing") : Direction.NORTH.get3DDataValue());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (Services.PLATFORM.getRegistry(Registries.BLOCK).contains(this.blockState.getBlock()))
            tag.put("stored_state", NbtUtils.writeBlockState(this.blockState));
        else
            tag.put("stored_state", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
        tag.putInt("facing", this.facing.get3DDataValue());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * A bridge placed from an item takes the block the item carries as {@code stored_state} in its
     * custom data. {@code BlockItem#place} hands the stack's components over here, and reading
     * {@code custom_data} marks it used, so it is not also kept on the block entity.
     */
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        super.applyImplicitComponents(components);
        CompoundTag data = components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (data.contains("stored_state", Tag.TAG_COMPOUND)) {
            this.setStoredBlockState(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), data.getCompound("stored_state")));
        }
    }

    /**
     * Asks the controller to close the gap when a segment is broken, called by {@link BridgeBlock}
     * as it goes. Only then can the segment's facing still be read, but it cannot restore itself
     * mid-removal, so the controller refills on its next tick.
     */
    public void onBlockRemoved(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        Direction towardsController = this.facing.getOpposite();
        int maxLength = BridgesCommonMod.COMMON_CONFIG.bridgeMaxLength.get();

        for (int i = 1; i <= maxLength; i++) {
            BlockPos check = pos.relative(towardsController, i);
            BlockState checkState = level.getBlockState(check);

            if (checkState.getBlock() instanceof BridgeControlBlock) {
                if (checkState.getValue(BridgeControlBlock.POWERED) && checkState.getValue(BridgeControlBlock.FACING) == this.facing
                        && level.getBlockEntity(check) instanceof BridgeControlBlockEntity controller) {
                    controller.requestGapFill();
                }

                return;
            }

            // Anything that is not another segment of the same run means this one was not projected
            // from a controller in that direction.
            if (!checkState.is(BridgesBlocks.BRIDGE.get())) {
                return;
            }
        }
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public BlockState getStoredBlockState() {
        return blockState;
    }

    public void setStoredBlockState(BlockState blockState) {
        this.blockState = blockState;

        if (level != null) {
            // Light dampening is baked into the block state, so a bridge carries its stored block's
            // in a property. Changing it is an ordinary block update, and vanilla does the rest:
            // relights, recomputes the sky column and sends the state to every client. Server only;
            // a client takes the state from that update. A bridge control inherits this and is not a
            // bridge, hence the block check.
            if (!level.isClientSide() && getBlockState().getBlock() instanceof BridgeBlock) {
                final BlockState lit = BridgeBlock.withStoredDampening(getBlockState(), blockState, level, worldPosition);
                if (lit != getBlockState()) {
                    level.setBlock(worldPosition, lit, Block.UPDATE_ALL);
                }
            }
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            // Emission is still read from the stored block per position, so the light has to be
            // asked to look again.
            level.getLightEngine().checkBlock(getBlockPos());
            if (!level.isClientSide()) {
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            } else {
                ClientServices.MODELS.requestModelDataRefresh(this);
            }
        }

        this.setChanged();
    }

    public void setStoredBlockState(String registryName) {
        this.setStoredBlockState(Services.PLATFORM.getRegistry(Registries.BLOCK).getValue(ResourceLocation.parse(registryName)).orElse(Blocks.AIR).defaultBlockState());
    }

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(BridgesModelProperties.BLOCK_STATE, blockState).build();
    }
}
