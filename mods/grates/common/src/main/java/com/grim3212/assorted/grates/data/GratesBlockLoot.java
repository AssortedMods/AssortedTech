package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GratesBlockLoot extends LibBlockLootProvider {

    public GratesBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> GratesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        GratesBlocks.allItemGrates().forEach(grate -> this.dropSelf(grate.get()));
    }
}
