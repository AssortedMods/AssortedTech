package com.grim3212.assorted.bridges.client.model;

import com.google.common.collect.ImmutableMap;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The json bridge: the {@code "bridge"} object names a parent model, re-baked per stored block state
 * with that block's texture in the {@code stored} slot. Each bake is a child model of that parent
 * holding only texture overrides, the same as a json that names the parent and sets {@code textures}
 * would be, so the parent's geometry, inheritance and display settings all apply.
 */
public class BridgeBakedModel extends BridgeBaseBakedModel {

    private final UnbakedModel parent;

    public BridgeBakedModel(IModelBakingContext context, BridgeUnbakedModel.Bridge unbakedBridge, UnbakedModel parent, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation name) {
        super(context, unbakedBridge, bakery, spriteGetter, transform, name);
        this.parent = parent;
    }

    @Override
    protected BakedModel generateModel(ImmutableMap<String, String> textures) {
        Map<String, Either<Material, String>> slots = new HashMap<>();
        this.model.textures().forEach((slot, texture) -> slots.put(slot, slot(texture)));
        textures.forEach((slot, texture) -> slots.put(slot, slot(texture)));

        BlockModel child = new BlockModel(this.model.parent(), List.of(), slots, null, null, ItemTransforms.NO_TRANSFORMS, List.of());
        child.name = this.name.toString();
        // Only the child's own parent is looked up: the parent's chain was resolved when this baked.
        child.resolveParents(location -> location.equals(this.model.parent()) ? this.parent : this.bakery.getModel(location));
        return child.bake(this.bakery, child, this.spriteGetter, this.transform, true);
    }

    private static Either<Material, String> slot(String texture) {
        return texture.startsWith("#") ? Either.right(texture.substring(1)) : Either.left(blockMaterial(texture));
    }
}
