package com.grim3212.assorted.elevators.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.elevators.client.model.CamouflageUnbakedModel;
import com.grim3212.assorted.lib.client.data.LibCustomLoaderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assortedelevators:camouflage} loader block: the {@code fallback} model drawn while
 * undisguised. Used as {@code builder.customLoader(CamouflageModelBuilder::begin)}.
 */
public class CamouflageModelBuilder<T extends ModelBuilder<T>> extends LibCustomLoaderBuilder<T> {

    public static <T extends ModelBuilder<T>> CamouflageModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CamouflageModelBuilder<>(parent, existingFileHelper);
    }

    private @Nullable ResourceLocation fallback;

    protected CamouflageModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(CamouflageUnbakedModel.LOADER_NAME, parent, existingFileHelper, false);
    }

    public CamouflageModelBuilder<T> fallback(ResourceLocation fallback) {
        this.fallback = fallback;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        Preconditions.checkNotNull(this.fallback, "fallback must not be null");
        json.addProperty("fallback", this.fallback.toString());
        return json;
    }
}
