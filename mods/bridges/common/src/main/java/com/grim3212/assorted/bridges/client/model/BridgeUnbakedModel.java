package com.grim3212.assorted.bridges.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BridgeUnbakedModel implements IModelSpecification<BridgeUnbakedModel> {

    public static final ResourceLocation LOADER_NAME = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "bridge");

    private final Bridge unbakedBridge;

    private BridgeUnbakedModel(Bridge unbakedBridge) {
        this.unbakedBridge = unbakedBridge;
    }

    /**
     * The item form is drawn from the stack's stored block, so the json's own overrides are not used.
     */
    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        UnbakedModel parent = baker.getModel(this.unbakedBridge.parent());
        parent.resolveParents(baker::getModel);
        return new BridgeBakedModel(context, this.unbakedBridge, parent, baker, spriteGetter, modelState, this.unbakedBridge.parent());
    }

    /**
     * The shape template, {@code tinted_cube}, which no blockstate names: without its parents
     * resolved every bridge bakes to the missing model.
     */
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of(this.unbakedBridge.parent());
    }

    /**
     * The {@code "bridge"} object of a bridge model json: the parent model giving the shape, plus
     * the texture slots it still needs (in practice {@code stored}, overridden by the stored block
     * state). The same shape as AssortedDecor's {@code ColorizerUnbakedModel.Colorizer}.
     */
    public record Bridge(ResourceLocation parent, ImmutableMap<String, String> textures) {
    }

    public static final class Loader implements IModelSpecificationLoader<BridgeUnbakedModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public BridgeUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
            if (!jsonObject.has("bridge"))
                throw new JsonParseException("Bridge Loader requires a 'bridge' key holding the model to retexture.");

            JsonObject bridge = GsonHelper.getAsJsonObject(jsonObject, "bridge");
            ResourceLocation parent = ResourceLocation.parse(GsonHelper.getAsString(bridge, "parent"));

            ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
            if (bridge.has("textures")) {
                for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(bridge, "textures").entrySet()) {
                    textures.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            return new BridgeUnbakedModel(new Bridge(parent, textures.build()));
        }
    }
}
