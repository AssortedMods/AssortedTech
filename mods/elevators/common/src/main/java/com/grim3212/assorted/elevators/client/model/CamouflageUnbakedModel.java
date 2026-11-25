package com.grim3212.assorted.elevators.client.model;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * A camouflaged block's model json: {@code "fallback"} names the plain model drawn while it wears no disguise.
 */
public class CamouflageUnbakedModel implements IModelSpecification<CamouflageUnbakedModel> {

    public static final ResourceLocation LOADER_NAME = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "camouflage");

    private final ResourceLocation fallback;

    public CamouflageUnbakedModel(ResourceLocation fallback) {
        this.fallback = fallback;
    }

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        UnbakedModel model = baker.getModel(this.fallback);
        model.resolveParents(baker::getModel);
        return new CamouflageBakedModel(model.bake(baker, spriteGetter, modelState));
    }

    /** The plain model, which the blockstate never names itself: without it the camouflaged blocks bake to the missing model. */
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of(this.fallback);
    }
}
