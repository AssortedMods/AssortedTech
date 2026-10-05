package com.grim3212.assorted.spikes.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpikesBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public SpikesBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> SpikesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        SpikesBlocks.SPIKES.forEach((spike) -> this.dropSelf(spike.get()));
    }
}
