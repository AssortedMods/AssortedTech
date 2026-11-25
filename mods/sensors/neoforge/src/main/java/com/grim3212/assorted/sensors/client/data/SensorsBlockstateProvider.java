package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.block.GpsSensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Block states, block models and the block items' models. {@link SensorsItemModelProvider} owns the
 * GPS, the one item that is not a block, so the two never write the same file.
 */
public class SensorsBlockstateProvider extends BlockStateProvider {

    public SensorsBlockstateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Sensors block states";
    }

    @Override
    protected void registerStatesAndModels() {
        SensorsBlocks.SENSORS.forEach(sensor -> this.sensor(sensor.get()));

        this.gpsSensor(SensorsBlocks.GPS_SENSOR.get());
        this.gpsSensor(SensorsBlocks.UPGRADED_GPS_SENSOR.get());
    }

    /** The same texture on every face, lit while it sees something. */
    private void gpsSensor(Block b) {
        String name = name(b);
        ModelFile off = this.models().cubeAll("block/" + name, resource("block/" + name + "_off"));
        ModelFile on = this.models().cubeAll("block/" + name + "_active", resource("block/" + name + "_on"));

        this.getVariantBuilder(b).forAllStates(state -> ConfiguredModel.builder().modelFile(state.getValue(GpsSensorBlock.ACTIVE) ? on : off).build());
        this.simpleBlockItem(b, off);
    }

    /**
     * The face pointed the way the sensor looks, turned as vanilla turns a column with a facing: up
     * unturned, down flipped, and the four sides tipped over to face out.
     */
    private void sensor(Block b) {
        String name = name(b);
        ResourceLocation side = resource("block/sensors/" + name + "_side");

        ModelFile undetected = this.models().orientableVertical("block/sensors/" + name, side, resource("block/sensors/" + name + "_off"));
        ModelFile detected = this.models().orientableVertical("block/sensors/" + name + "_detected", side, resource("block/sensors/" + name + "_on"));
        ModelFile inventory = this.models().orientable("block/sensors/" + name + "_inventory", side, resource("block/sensors/" + name + "_off"), side);

        this.directionalBlock(b, state -> state.getValue(SensorBlock.DETECTED) ? detected : undetected, 180);
        this.simpleBlockItem(b, inventory);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
