package com.grim3212.assorted.sensors.client.blockentity;

import com.grim3212.assorted.sensors.common.block.blockentity.GpsSensorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

/**
 * Outlines the box a GPS sensor watches, green when it can see into it and red when the position is
 * solid or out of range. The box is up to the upgraded sensor's range away, so it is drawn even
 * when the sensor block itself is off screen.
 */
public class GpsSensorBlockEntityRenderer implements BlockEntityRenderer<GpsSensorBlockEntity> {

    public GpsSensorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GpsSensorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        GpsSensorBlockEntity.TargetStatus status = entity.targetStatus();
        // A position in another dimension has nowhere here to outline.
        if (!entity.isShowingTarget() || status == GpsSensorBlockEntity.TargetStatus.NONE || status == GpsSensorBlockEntity.TargetStatus.OTHER_DIMENSION) {
            return;
        }

        AABB box = entity.senseBox().move(-entity.getBlockPos().getX(), -entity.getBlockPos().getY(), -entity.getBlockPos().getZ()).inflate(0.002D);
        boolean obstructed = status != GpsSensorBlockEntity.TargetStatus.GOOD;
        LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), box, obstructed ? 1.0F : 0.0F, obstructed ? 0.0F : 1.0F, 0.0F, 1.0F);
    }

    @Override
    public boolean shouldRenderOffScreen(GpsSensorBlockEntity entity) {
        return true;
    }
}
