package com.grim3212.assorted.elevators.client.render;

import com.grim3212.assorted.elevators.common.block.ElevatorGroup;
import com.grim3212.assorted.elevators.common.entity.ElevatorCarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the moving car as its blocks, the way vanilla draws a falling block. Every cell is a solid
 * full block: an elevator, or a disguise, which has to be solid and opaque.
 */
public class ElevatorCarRenderer extends EntityRenderer<ElevatorCarEntity> {

    private final BlockRenderDispatcher dispatcher;

    public ElevatorCarRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ElevatorCarEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int sizeX = entity.getSizeX();
        int sizeZ = entity.getSizeZ();
        ElevatorGroup group = entity.getGroup();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                BlockState look = entity.getCellLook(x, z);
                if (look.getRenderShape() != RenderShape.MODEL) {
                    continue;
                }

                // Lit from the block its top is in, so the car does not go dark while its bottom is inside a floor.
                BlockPos pos = BlockPos.containing(group.minX() + x, entity.getBoundingBox().maxY, group.minZ() + z);
                poseStack.pushPose();
                poseStack.translate(x - sizeX / 2.0D, 0.0D, z - sizeZ / 2.0D);
                this.dispatcher.renderBatched(look, pos, entity.level(), poseStack, consumer, false, RandomSource.create(look.getSeed(pos)));
                poseStack.popPose();
            }
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElevatorCarEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
