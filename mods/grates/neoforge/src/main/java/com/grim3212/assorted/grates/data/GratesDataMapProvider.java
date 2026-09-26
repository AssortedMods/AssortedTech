package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.concurrent.CompletableFuture;

/**
 * Axe scraping and honeycomb waxing for the copper grates. Mirror of the {@code OxidizableBlocksRegistry}
 * calls in {@code AssortedGratesFabric}.
 */
public class GratesDataMapProvider extends DataMapProvider {

    public GratesDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        Builder<Oxidizable, Block> oxidizables = builder(NeoForgeDataMaps.OXIDIZABLES);
        GratesBlocks.COPPER_ITEM_GRATES.weathering().progressMapping((from, to) ->
                oxidizables.add(key(from), new Oxidizable(to.get()), false));

        Builder<Waxable, Block> waxables = builder(NeoForgeDataMaps.WAXABLES);
        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) ->
                waxables.add(key(unwaxed), new Waxable(waxed.get()), false));
    }

    private static ResourceKey<Block> key(IRegistryObject<? extends Block> block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block.get()));
    }
}
