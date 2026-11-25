package com.grim3212.assorted.elevators.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class CamouflageModelLoader implements IModelSpecificationLoader<CamouflageUnbakedModel> {

    public static final CamouflageModelLoader INSTANCE = new CamouflageModelLoader();

    @Override
    public CamouflageUnbakedModel read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) {
        return new CamouflageUnbakedModel(ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "fallback")));
    }
}
