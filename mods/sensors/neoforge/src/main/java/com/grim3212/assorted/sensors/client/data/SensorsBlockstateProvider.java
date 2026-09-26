package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.block.GpsSensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and every block item
 * {@link SensorsItemModelProvider} does not claim, so the two never write the same file.
 */
public class SensorsBlockstateProvider extends ModelProvider {

    public SensorsBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Sensors block states";
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem && !SensorsItemModelProvider.owns(holder.value()));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        SensorsBlocks.SENSORS.forEach(sensor -> sensor(blockModels, sensor.get()));

        gpsSensor(blockModels, SensorsBlocks.GPS_SENSOR.get());
        gpsSensor(blockModels, SensorsBlocks.UPGRADED_GPS_SENSOR.get());
    }

    /** The same texture on every face, lit while it sees something. */
    private void gpsSensor(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Identifier off = ModelTemplates.CUBE_ALL.create(resource("block/" + name), TextureMapping.cube(texture("block/" + name + "_off")), blockModels.modelOutput);
        Identifier on = ModelTemplates.CUBE_ALL.create(resource("block/" + name + "_active"), TextureMapping.cube(texture("block/" + name + "_on")), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(GpsSensorBlock.ACTIVE, BlockModelGenerators.plainVariant(on), BlockModelGenerators.plainVariant(off))));

        blockModels.registerSimpleItemModel(b, off);
    }

    private void sensor(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Material side = texture("block/sensors/" + name + "_side");

        Identifier undetected = orientableVertical(blockModels, "block/sensors/" + name, texture("block/sensors/" + name + "_off"), side);
        Identifier detected = orientableVertical(blockModels, "block/sensors/" + name + "_detected", texture("block/sensors/" + name + "_on"), side);
        Identifier inventory = orientable(blockModels, "block/sensors/" + name + "_inventory", texture("block/sensors/" + name + "_off"), side);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(SensorBlock.DETECTED, BlockModelGenerators.plainVariant(detected), BlockModelGenerators.plainVariant(undetected)))
                .with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING));

        blockModels.registerSimpleItemModel(b, inventory);
    }

    // ------------------------------------------------------------------ helpers

    private Identifier orientable(BlockModelGenerators blockModels, String path, Material front, Material side) {
        return ModelTemplates.CUBE_ORIENTABLE.create(resource(path), new TextureMapping()
                .put(TextureSlot.FRONT, front).put(TextureSlot.SIDE, side).put(TextureSlot.TOP, side), blockModels.modelOutput);
    }

    /**
     * {@code orientable_vertical} takes only {@code front} and {@code side}. The 1.20.1 builder also
     * wrote a {@code top} entry into these models; the template does not, because nothing reads it.
     */
    private Identifier orientableVertical(BlockModelGenerators blockModels, String path, Material front, Material side) {
        return ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(resource(path), new TextureMapping()
                .put(TextureSlot.FRONT, front).put(TextureSlot.SIDE, side), blockModels.modelOutput);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }
}
