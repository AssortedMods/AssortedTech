package com.grim3212.assorted.redstone.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RedstoneBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public RedstoneBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> RedstoneBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.dropSelf(RedstoneBlocks.FLIP_FLOP_TORCH.get());
        this.dropSelf(RedstoneBlocks.GLOWSTONE_TORCH.get());
        this.dropOther(RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get(), RedstoneBlocks.FLIP_FLOP_TORCH.get());
        this.dropOther(RedstoneBlocks.GLOWSTONE_WALL_TORCH.get(), RedstoneBlocks.GLOWSTONE_TORCH.get());
    }
}
