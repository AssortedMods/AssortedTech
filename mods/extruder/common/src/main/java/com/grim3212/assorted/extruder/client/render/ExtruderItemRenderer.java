package com.grim3212.assorted.extruder.client.render;

import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Draws an extruder item as the extruder itself, the entity's model and its material's texture,
 * posed as {@link ExtruderRenderer} poses one facing north. The item's model is special
 * ({@code builtin/entity}) with {@code block/block}'s display settings, where north is the face
 * shown on the left in the inventory, as a furnace shows its front, so the drill is seen there.
 */
public class ExtruderItemRenderer extends BlockEntityWithoutLevelRenderer {

    private final ExtruderType type;
    private ExtruderModel model;

    public ExtruderItemRenderer(ExtruderType type) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.type = type;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.model = new ExtruderModel(Minecraft.getInstance().getEntityModels().bakeLayer(ExtruderModel.LAYER));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (this.model == null) {
            this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        }

        poseStack.pushPose();
        // Into the middle of the item's block-sized space, the drill turned north, and the usual entity model flip.
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        // A resting extruder: the drill drawn in.
        this.model.setupAnim(0.0F);
        this.model.renderToBuffer(poseStack, ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(ExtruderRenderer.texture(this.type)), false, stack.hasFoil()), packedLight, packedOverlay);
        poseStack.popPose();
    }
}
