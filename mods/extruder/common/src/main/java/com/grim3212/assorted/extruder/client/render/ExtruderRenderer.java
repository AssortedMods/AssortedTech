package com.grim3212.assorted.extruder.client.render;

import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.common.entity.ExtruderEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The model's drill points along its +X, which after the usual flip of an entity model is west in
 * the world; the pose turns west to the extruder's facing. Hits wobble it like a boat.
 */
public class ExtruderRenderer extends EntityRenderer<ExtruderEntity> {

    /** Each material's drill is coloured to match its spikes. */
    private static final Map<ExtruderType, ResourceLocation> TEXTURES = Arrays.stream(ExtruderType.values())
            .collect(Collectors.toMap(Function.identity(), type -> ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/extruders/" + type + ".png"), (a, b) -> a, () -> new EnumMap<>(ExtruderType.class)));

    private final ExtruderModel model;

    /** Also what the item draws, through {@link ExtruderItemRenderer}. */
    public static ResourceLocation texture(ExtruderType type) {
        return TEXTURES.get(type);
    }

    public ExtruderRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ExtruderModel(context.bakeLayer(ExtruderModel.LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(ExtruderEntity entity) {
        return TEXTURES.get(entity.getExtruderType());
    }

    @Override
    public void render(ExtruderEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBbHeight() / 2.0F, 0.0F);

        switch (entity.getFacing()) {
            case UP -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            case DOWN -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            default -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - entity.getFacing().toYRot()));
        }

        float hurtTime = entity.getHurtTime() - partialTicks;
        float damage = Math.max(entity.getDamage() - partialTicks, 0.0F);
        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * entity.getHurtDir()));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(entity.isRunning() ? Mth.sin((entity.tickCount + partialTicks) * 0.6F) : 0.0F);
        this.model.renderToBuffer(poseStack, buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }
}
