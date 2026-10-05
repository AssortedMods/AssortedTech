package com.grim3212.assorted.fan.data;

import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FanBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public FanBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> FanBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.dropSelf(FanBlocks.FAN.get());
    }
}
