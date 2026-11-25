package com.grim3212.assorted.fan.client.data;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.common.block.FanBlock;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * The fan's block states, block models and item model.
 */
public class FanBlockstateProvider extends BlockStateProvider {

    public FanBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Constants.MOD_ID, exFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Fan block states";
    }

    @Override
    protected void registerStatesAndModels() {
        fan();
    }

    /**
     * An orientable model per mode, and a vertical one for facing up or down. A horizontal facing
     * is turned to face the player as vanilla's furnace is.
     */
    private void fan() {
        Block b = FanBlocks.FAN.get();
        String name = name(b);
        ResourceLocation noteBlock = mcLoc("block/note_block");

        ModelFile blow = this.models().orientable(name + "_blow", noteBlock, texture("block/" + name), noteBlock);
        ModelFile suck = this.models().orientable(name + "_suck", noteBlock, texture("block/" + name + "_suck"), noteBlock);
        ModelFile stopped = this.models().orientable(name + "_stopped", noteBlock, texture("block/" + name + "_stopped"), noteBlock);

        ModelFile blowVertical = this.models().orientableVertical(name + "_blow_vertical", noteBlock, texture("block/" + name));
        ModelFile suckVertical = this.models().orientableVertical(name + "_suck_vertical", noteBlock, texture("block/" + name + "_suck"));
        ModelFile stoppedVertical = this.models().orientableVertical(name + "_stopped_vertical", noteBlock, texture("block/" + name + "_stopped"));

        this.getVariantBuilder(b).forAllStates(state -> {
            Direction dir = state.getValue(FanBlock.FACING);
            boolean vertical = dir.getAxis().isVertical();
            ModelFile model = switch (state.getValue(FanBlock.MODE)) {
                case BLOW -> vertical ? blowVertical : blow;
                case SUCK -> vertical ? suckVertical : suck;
                case OFF -> vertical ? stoppedVertical : stopped;
            };
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(dir == Direction.DOWN ? 180 : 0)
                    .rotationY(vertical ? 0 : ((int) dir.toYRot() + 180) % 360).build();
        });

        this.simpleBlockItem(b, stopped);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static ResourceLocation texture(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
