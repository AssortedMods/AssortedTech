package com.grim3212.assorted.grates;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;

public class AssortedGratesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        GratesCommonMod.init();

        registerCopperItemGrateOxidation();
    }

    /**
     * How the copper grates scrape back with an axe and wax with a honeycomb on Fabric. NeoForge's
     * half is {@code GratesDataMapProvider}, so a change here needs the same change there.
     */
    private static void registerCopperItemGrateOxidation() {
        GratesBlocks.COPPER_ITEM_GRATES.weathering().progressMapping((from, to) ->
                OxidizableBlocksRegistry.registerNextStage(from.get(), to.get()));
        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) ->
                OxidizableBlocksRegistry.registerWaxable(unwaxed.get(), waxed.get()));
    }
}
