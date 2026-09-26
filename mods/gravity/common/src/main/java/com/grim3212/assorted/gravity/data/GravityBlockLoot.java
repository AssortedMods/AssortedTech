package com.grim3212.assorted.gravity.data;

import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GravityBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    // Loot sub providers are handed the registry lookup at construction now.
    public GravityBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> GravityBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
        this.blocks.add(GravityBlocks.METAL_MESH.get());

        this.blocks.add(GravityBlocks.ATTRACTOR.get());
        this.blocks.add(GravityBlocks.REPULSOR.get());
        this.blocks.add(GravityBlocks.GRAVITOR.get());
        this.blocks.add(GravityBlocks.ATTRACTOR_DIRECTIONAL.get());
        this.blocks.add(GravityBlocks.REPULSOR_DIRECTIONAL.get());
        this.blocks.add(GravityBlocks.GRAVITOR_DIRECTIONAL.get());
    }

    @Override
    public void generate() {
        for (Block b : this.blocks) {
            this.dropSelf(b);
        }
    }

}
