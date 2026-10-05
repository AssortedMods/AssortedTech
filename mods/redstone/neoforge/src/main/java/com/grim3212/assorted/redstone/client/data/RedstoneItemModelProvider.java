package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * The two torch items, whose inventory model is a flat sprite from {@code block/}. That is why
 * {@code generateFlatItem}, which takes the texture from the item id, is not used.
 */
public class RedstoneItemModelProvider extends ModelProvider {

    public RedstoneItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Redstone item models";
    }

    /** This provider registers no blocks at all; {@link RedstoneBlockstateProvider} owns every one. */
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        flatItem(itemModels, RedstoneItems.FLIP_FLOP_TORCH.get(), "block/flip_flop_torch_off");
        flatItem(itemModels, RedstoneItems.GLOWSTONE_TORCH.get(), "block/glowstone_torch_off");
    }

    private void flatItem(ItemModelGenerators itemModels, Item item, String texture) {
        Identifier model = ModelTemplates.FLAT_ITEM.create(modelId(name(item)), TextureMapping.layer0(prefixed(texture)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private static Identifier modelId(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item/" + path);
    }

    private static Material prefixed(String texture) {
        return new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, texture));
    }
}
