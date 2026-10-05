package com.grim3212.assorted.grates.client.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

/**
 * Block states, block models and item models for every item grate.
 */
public class GratesBlockstateProvider extends ModelProvider {

    public GratesBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Grates block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        GratesBlocks.ITEM_GRATES.values().forEach(grate -> blockModels.createTrivialCube(grate.get()));

        // A waxed grate looks the same as its stage, so it points at that stage's model as vanilla's waxed copper does.
        GratesBlocks.COPPER_ITEM_GRATES.weathering().forEach(grate -> blockModels.createTrivialCube(grate.get()));
        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) -> blockModels.copyModel(unwaxed.get(), waxed.get()));
    }
}
