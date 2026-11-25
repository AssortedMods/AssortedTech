package com.grim3212.assorted.gravity.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;

/** The wireframe both gravity renderers draw around a field's range. */
final class RangeOutline {

    private RangeOutline() {
    }

    /** Draws {@code box}, in block-local coordinates: red when obstructed, green when clear. */
    static void draw(PoseStack poseStack, MultiBufferSource bufferSource, AABB box, boolean obstructed) {
        float red = obstructed ? 1.0F : 0.0F;
        float green = obstructed ? 0.0F : 1.0F;
        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), box, red, green, 0.0F, 1.0F);
    }
}
