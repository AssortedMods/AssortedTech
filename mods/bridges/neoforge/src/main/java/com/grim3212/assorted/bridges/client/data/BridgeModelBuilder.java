package com.grim3212.assorted.bridges.client.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.grim3212.assorted.bridges.client.model.BridgeUnbakedModel;
import com.grim3212.assorted.lib.client.data.LibCustomLoaderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Writes the {@code assortedbridges:bridge} loader block into a bridge block model json: a
 * {@code parent} and the {@code textures} map {@link BridgeUnbakedModel.Loader} reads. The loader
 * replaces the geometry, so it takes no inline elements. Used as
 * {@code builder.customLoader(BridgeModelBuilder::begin)}.
 */
public class BridgeModelBuilder<T extends ModelBuilder<T>> extends LibCustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> BridgeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new BridgeModelBuilder<>(parent, existingFileHelper);
    }

    private @Nullable ResourceLocation bridge;
    private final Map<String, ResourceLocation> textures = Maps.newLinkedHashMap();

    protected BridgeModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(BridgeUnbakedModel.LOADER_NAME, parent, existingFileHelper, false);
    }

    /**
     * The model whose shape and remaining texture slots the bridge inherits.
     */
    public BridgeModelBuilder<T> bridge(ResourceLocation bridge) {
        Preconditions.checkNotNull(bridge, "bridge must not be null");
        this.bridge = bridge;
        return this;
    }

    /**
     * A texture slot filled in on the inherited model. The slot name carries no leading {@code #}:
     * that marks a reference to another slot, so {@code "#stored"} would declare a slot nothing could
     * ever find.
     */
    public BridgeModelBuilder<T> addTexture(String name, ResourceLocation texture) {
        Preconditions.checkNotNull(texture, "texture must not be null");
        this.textures.put(name, texture);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        Preconditions.checkNotNull(bridge, "bridge must not be null");

        JsonObject bridgeObj = new JsonObject();
        bridgeObj.addProperty("parent", bridge.toString());

        if (!this.textures.isEmpty()) {
            JsonObject textureObj = new JsonObject();
            this.textures.forEach((k, v) -> textureObj.addProperty(k, v.toString()));
            bridgeObj.add("textures", textureObj);
        }

        json.add("bridge", bridgeObj);
        return json;
    }
}
