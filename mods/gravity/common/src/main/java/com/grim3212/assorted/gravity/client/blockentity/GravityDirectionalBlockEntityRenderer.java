package com.grim3212.assorted.gravity.client.blockentity;

import com.grim3212.assorted.gravity.common.block.GravityDirectionalBlock;
import com.grim3212.assorted.gravity.common.block.blockentity.GravityDirectionalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** The directional gravity field's range box, red when a full face cuts it short and green when clear. */
public class GravityDirectionalBlockEntityRenderer implements BlockEntityRenderer<GravityDirectionalBlockEntity> {

    public GravityDirectionalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GravityDirectionalBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = entity.getLevel();
        if (!entity.shouldShowRange() || level == null) {
            return;
        }

        Direction dir = entity.getBlockState().getValue(GravityDirectionalBlock.FACING);

        // The plus 1 is because we deflate by 1 so we don't include the block itself
        int maxLength = entity.getRange() + 1;

        boolean obstructed = false;
        int traverse = 1;
        while (traverse < maxLength && !obstructed) {
            BlockPos checkPos = entity.getBlockPos().relative(dir, traverse);
            BlockState checkState = level.getBlockState(checkPos);
            if (Block.isFaceFull(checkState.getCollisionShape(level, checkPos), dir.getOpposite())) {
                obstructed = true;
            } else {
                traverse++;
            }
        }

        Vec3i position = dir.getNormal().multiply(obstructed ? traverse : maxLength);
        AABB aabb = entity.getBlockState().getCollisionShape(level, entity.getBlockPos()).bounds().expandTowards(position.getX(), position.getY(), position.getZ()).deflate(1D).inflate(0.0020000000949949026D);
        RangeOutline.draw(poseStack, bufferSource, aabb, obstructed);
    }

    /** The box reaches past the block itself, so it must still draw when the block is just off screen. */
    @Override
    public boolean shouldRenderOffScreen(GravityDirectionalBlockEntity entity) {
        return entity.shouldShowRange();
    }
}
