package com.grim3212.assorted.tech.client.model;

import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.tech.Constants;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

/**
 * A camouflaged block's model json: {@code "fallback"} names the plain model drawn while it wears no disguise.
 */
public class CamouflageUnbakedModel implements IModelSpecification<CamouflageUnbakedModel> {

    public static final Identifier LOADER_NAME = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "camouflage");

    private final Identifier fallback;

    public CamouflageUnbakedModel(Identifier fallback) {
        this.fallback = fallback;
    }

    @Override
    public BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation) {
        ResolvedModel model = baker.getModel(this.fallback);
        TextureSlots slots = model.getTopTextureSlots();
        BlockStateModel fallback = new SingleVariant(new SimpleModelWrapper(model.bakeTopGeometry(slots, baker, modelState), model.getTopAmbientOcclusion(), model.resolveParticleMaterial(slots, baker)));
        return new CamouflageBakedModel(fallback);
    }

    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.fallback);
    }
}
