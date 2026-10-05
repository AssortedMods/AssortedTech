package com.grim3212.assorted.extruder.client.data;

import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.client.render.ExtruderRenderer;
import com.grim3212.assorted.extruder.client.render.ExtruderSpecialRenderer;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.stream.Stream;

/** The extruder items, each drawn as the extruder itself. */
public class ExtruderItemModelProvider extends ModelProvider {

    public ExtruderItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return Constants.MOD_NAME + " item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        ExtruderItems.EXTRUDERS.forEach((type, extruder) -> extruder(itemModels, extruder.get(), type));
    }

    /**
     * The extruder in 3D, drawn by {@link ExtruderSpecialRenderer}. The base model holds only the display
     * settings and the particle, which has to be an atlas sprite rather than the entity texture.
     */
    private void extruder(ItemModelGenerators itemModels, Item item, ExtruderType type) {
        TextureMapping particle = new TextureMapping().put(TextureSlot.PARTICLE, prefixed("item/extruder_particle"));
        Identifier base = EXTRUDER_BASE.create(modelId(name(item)), particle, itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.specialModel(base, new ExtruderSpecialRenderer.Unbaked(ExtruderRenderer.texture(type))));
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private static final ModelTemplate EXTRUDER_BASE = ExtendedModelTemplateBuilder.builder()
            .parent(Identifier.withDefaultNamespace("block/block"))
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();

    private static Identifier modelId(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item/" + path);
    }

    private static Material prefixed(String texture) {
        return new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, texture));
    }
}
