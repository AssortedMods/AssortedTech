package com.grim3212.assorted.elevators.common.block.blockentity;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.elevators.common.block.CamouflageBlock;
import com.grim3212.assorted.elevators.common.properties.ElevatorsModelProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

/** The disguise a {@link CamouflageBlock} wears, air for none. */
public class CamouflageBlockEntity extends BlockEntity implements IBlockEntityWithModelData {

    private BlockState disguise = Blocks.AIR.defaultBlockState();

    public CamouflageBlockEntity(BlockPos pos, BlockState state) {
        super(ElevatorsBlockEntityTypes.CAMOUFLAGE.get(), pos, state);
    }

    public BlockState getDisguise() {
        return this.disguise;
    }

    public void setDisguise(BlockState disguise) {
        this.disguise = disguise;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            // Emission comes from the disguise, which no block state change tells the light engine about.
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        BlockState read = tag.contains("disguise", Tag.TAG_COMPOUND)
                ? NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("disguise"))
                : Blocks.AIR.defaultBlockState();
        // One a mod update stopped allowing comes off rather than drawing wrong.
        this.disguise = CamouflageBlock.canDisguiseAs(read) ? read : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.disguise.isAir()) {
            tag.put("disguise", NbtUtils.writeBlockState(this.disguise));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(ElevatorsModelProperties.BLOCK_STATE, this.disguise).build();
    }
}
