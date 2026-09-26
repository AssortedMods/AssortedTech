package com.grim3212.assorted.tech.client.render;

import com.grim3212.assorted.tech.common.block.ElevatorGroup;
import com.grim3212.assorted.tech.common.entity.ElevatorCarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;

/**
 * Draws the moving car as its blocks, the way vanilla draws a falling block.
 */
public class ElevatorCarRenderer extends EntityRenderer<ElevatorCarEntity, ElevatorCarRenderer.ElevatorCarRenderState> {

    public ElevatorCarRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void submit(ElevatorCarRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (int x = 0; x < state.sizeX; x++) {
            for (int z = 0; z < state.sizeZ; z++) {
                poseStack.pushPose();
                poseStack.translate(x - state.sizeX / 2.0D, 0.0D, z - state.sizeZ / 2.0D);
                submitNodeCollector.submitMovingBlock(poseStack, state.cells[ElevatorCarEntity.cellIndex(x, z)], state.outlineColor);
                poseStack.popPose();
            }
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public ElevatorCarRenderState createRenderState() {
        return new ElevatorCarRenderState();
    }

    @Override
    public void extractRenderState(ElevatorCarEntity entity, ElevatorCarRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.sizeX = entity.getSizeX();
        state.sizeZ = entity.getSizeZ();
        ElevatorGroup group = entity.getGroup();
        for (int x = 0; x < state.sizeX; x++) {
            for (int z = 0; z < state.sizeZ; z++) {
                // Lit from the block its top is in, so the car does not go dark while its bottom is inside a floor.
                BlockPos pos = BlockPos.containing(group.minX() + x, entity.getBoundingBox().maxY, group.minZ() + z);
                MovingBlockRenderState cell = state.cells[ElevatorCarEntity.cellIndex(x, z)];
                cell.randomSeedPos = pos;
                cell.blockPos = pos;
                cell.blockState = entity.getCellLook(x, z);
                if (entity.level() instanceof ClientLevel clientLevel) {
                    cell.biome = clientLevel.getBiome(pos);
                    cell.cardinalLighting = clientLevel.cardinalLighting();
                    cell.lightEngine = clientLevel.getLightEngine();
                }
            }
        }
    }

    public static class ElevatorCarRenderState extends EntityRenderState {
        /** One per cell, as each submitted block keeps its state until the frame is drawn. */
        public final MovingBlockRenderState[] cells = new MovingBlockRenderState[ElevatorGroup.MAX_SIZE * ElevatorGroup.MAX_SIZE];
        public int sizeX = 1;
        public int sizeZ = 1;

        public ElevatorCarRenderState() {
            for (int i = 0; i < this.cells.length; i++) {
                this.cells[i] = new MovingBlockRenderState();
            }
        }
    }
}
