package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BridgesBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    // Loot sub providers are handed the registry lookup at construction now.
    public BridgesBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> BridgesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
        this.blocks.add(BridgesBlocks.BRIDGE_CONTROL_ACCEL.get());
        this.blocks.add(BridgesBlocks.BRIDGE_CONTROL_DEATH.get());
        this.blocks.add(BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get());
        this.blocks.add(BridgesBlocks.BRIDGE_CONTROL_LASER.get());
        this.blocks.add(BridgesBlocks.BRIDGE_CONTROL_TRICK.get());
    }

    @Override
    public void generate() {
        for (Block b : this.blocks) {
            this.dropSelf(b);
        }
    }

}
