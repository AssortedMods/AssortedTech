package com.grim3212.assorted.sensors.client.blockentity;

import com.grim3212.assorted.sensors.common.block.blockentity.SensorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

/**
 * The sensor's detection box, the same box {@link SensorBlockEntity#tick} looks in: green while
 * clear, red once a full block face stops it short of its range.
 */
public class SensorBlockEntityRenderer implements BlockEntityRenderer<SensorBlockEntity> {

    public SensorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SensorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Level level = entity.getLevel();
        if (!entity.shouldShowRange() || level == null) {
            return;
        }

        Direction dir = entity.getBlockState().getValue(BlockStateProperties.FACING);

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
        AABB box = entity.getBlockState().getCollisionShape(level, entity.getBlockPos()).bounds().expandTowards(position.getX(), position.getY(), position.getZ()).deflate(1D).inflate(0.0020000000949949026D);
        LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), box, obstructed ? 1.0F : 0.0F, obstructed ? 0.0F : 1.0F, 0.0F, 1.0F);
    }

    /** The box reaches up to fifteen blocks out, so it is drawn while its far end is in view. */
    @Override
    public boolean shouldRenderOffScreen(SensorBlockEntity entity) {
        return entity.shouldShowRange();
    }
}
