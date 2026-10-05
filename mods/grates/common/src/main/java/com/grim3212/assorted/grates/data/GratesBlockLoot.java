package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GratesBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public GratesBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> GratesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        GratesBlocks.allItemGrates().forEach(grate -> this.dropSelf(grate.get()));
    }
}
