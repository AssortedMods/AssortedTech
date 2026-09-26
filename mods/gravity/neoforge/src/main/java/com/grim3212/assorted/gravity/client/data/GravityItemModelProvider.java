package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.item.GravityItems;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/** Item models for the items not drawn from a block model, which is only the gravity boots. */
public class GravityItemModelProvider extends ModelProvider {

    public GravityItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Gravity item models";
    }

    /**
     * This provider registers no blocks at all; {@link GravityBlockstateProvider} owns every one.
     */
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    /** Everything but the block items, the other half of the filter {@link GravityBlockstateProvider} applies. */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        flatItem(itemModels, GravityItems.GRAVITY_BOOTS.get(), "item/gravity_boots");
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
