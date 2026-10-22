package com.grim3212.assorted.fan.data;

import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FanBlockLoot extends LibBlockLootProvider {

    public FanBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> FanBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.dropSelf(FanBlocks.FAN.get());
    }
}
