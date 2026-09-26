package com.grim3212.assorted.tech.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.data.LibCustomLoaderBuilder;
import com.grim3212.assorted.tech.client.model.CamouflageUnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assortedtech:camouflage} loader block: the {@code fallback} model drawn while undisguised.
 */
public class CamouflageModelBuilder extends LibCustomLoaderBuilder {

    public static CamouflageModelBuilder begin() {
        return new CamouflageModelBuilder();
    }

    private @Nullable Identifier fallback;

    protected CamouflageModelBuilder() {
        super(CamouflageUnbakedModel.LOADER_NAME, false);
    }

    public CamouflageModelBuilder fallback(Identifier fallback) {
        this.fallback = fallback;
        return this;
    }

    @Override
    protected CustomLoaderBuilder copyInternal() {
        CamouflageModelBuilder copy = new CamouflageModelBuilder();
        copy.fallback = this.fallback;
        return copy;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        Preconditions.checkNotNull(this.fallback, "fallback must not be null");
        json.addProperty("fallback", this.fallback.toString());
        return json;
    }
}
