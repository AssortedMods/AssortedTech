package com.grim3212.assorted.grates.client.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Block states, block models and item models for every item grate.
 */
public class GratesBlockstateProvider extends BlockStateProvider {

    /** Every grate texture is see-through in its holes and opaque elsewhere. */
    private static final String CUTOUT = "minecraft:cutout";

    public GratesBlockstateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Grates block states";
    }

    @Override
    protected void registerStatesAndModels() {
        GratesBlocks.ITEM_GRATES.values().forEach(grate -> this.grate(grate.get()));

        // A waxed grate looks the same as its stage, so it points at that stage's model as vanilla's waxed copper does.
        GratesBlocks.COPPER_ITEM_GRATES.weathering().forEach(grate -> this.grate(grate.get()));
        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) ->
                this.simpleBlockWithItem(waxed.get(), new ModelFile.UncheckedModelFile(this.blockTexture(unwaxed.get()))));
    }

    private void grate(Block block) {
        ModelFile model = this.models().cubeAll(BuiltInRegistries.BLOCK.getKey(block).getPath(), this.blockTexture(block)).renderType(CUTOUT);
        this.simpleBlockWithItem(block, model);
    }
}
