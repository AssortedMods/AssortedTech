package com.grim3212.assorted.extruder.client.render;

import com.grim3212.assorted.extruder.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** The extruder's body and its two drill pieces, the outer one of which works in and out as it runs. */
public class ExtruderModel extends Model {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "extruder"), "main");

    private static final float DRILL_X = 3.0F;

    private final ModelPart root;
    private final ModelPart drill;

    public ExtruderModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.root = root;
        this.drill = root.getChild("drill");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-7.0F, -8.0F, -7.0F, 13.0F, 15.0F, 14.0F), PartPose.ZERO);
        root.addOrReplaceChild("drill_head", CubeListBuilder.create().texOffs(0, 29).mirror().addBox(0.0F, -8.0F, 0.0F, 3.0F, 12.0F, 12.0F), PartPose.offset(5.0F, 2.0F, -6.0F));
        root.addOrReplaceChild("drill", CubeListBuilder.create().texOffs(30, 41).mirror().addBox(0.0F, -8.0F, 0.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(DRILL_X, 5.0F, -3.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** Where the outer drill piece is in its stroke, from -1 drawn in to 1 pushed out; 0 at rest. */
    public void setupAnim(float drillWave) {
        this.drill.x = DRILL_X + drillWave * 2.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
