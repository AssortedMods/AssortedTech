package com.grim3212.assorted.alarm.data;

import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AlarmBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public AlarmBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> AlarmBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.dropSelf(AlarmBlocks.ALARM.get());
    }
}
