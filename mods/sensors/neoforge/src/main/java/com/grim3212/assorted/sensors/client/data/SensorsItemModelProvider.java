package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
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

import java.util.List;
import java.util.stream.Stream;

/** Item models for the items not drawn from a block model, which is only the GPS. */
public class SensorsItemModelProvider extends ModelProvider {

    public SensorsItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    /**
     * The items this provider claims. Read by {@link SensorsBlockstateProvider} so the two never write
     * the same file.
     */
    private static List<Item> owned() {
        return List.of(SensorsItems.GPS.get());
    }

    public static boolean owns(Item item) {
        return owned().contains(item);
    }

    @Override
    public String getName() {
        return "Assorted Sensors item models";
    }

    /**
     * This provider registers no blocks at all; {@link SensorsBlockstateProvider} owns every one.
     */
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    /**
     * Filtered out of the registry rather than built from {@code Item#builtInRegistryHolder()}, which
     * is deprecated, and symmetrical with the filter {@link SensorsBlockstateProvider} applies.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> owns(holder.value()));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        flatItem(itemModels, SensorsItems.GPS.get(), "item/gps");
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
