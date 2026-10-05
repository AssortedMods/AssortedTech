package com.grim3212.assorted.spikes.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.api.SpikesTags;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SpikesBlockTagProvider extends LibBlockTagProvider {

    public SpikesBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // TagAppender only accepts ResourceKeys, so each spike goes in by its key.
        for (IRegistryObject<SpikeBlock> b : SpikesBlocks.SPIKES) {
            appender.apply(SpikesTags.Blocks.SPIKES).add(BuiltInRegistries.BLOCK.getResourceKey(b.get()).orElseThrow());
            appender.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(b.get()).orElseThrow());
        }
    }
}
