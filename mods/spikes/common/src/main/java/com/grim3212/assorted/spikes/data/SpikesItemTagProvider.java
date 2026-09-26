package com.grim3212.assorted.spikes.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.api.SpikesTags;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpikesItemTagProvider extends LibItemTagProvider {

    public SpikesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // TagAppender only accepts ResourceKeys, so each spike goes in by its key.
        for (IRegistryObject<SpikeBlock> b : SpikesBlocks.SPIKES) {
            appender.apply(SpikesTags.Items.SPIKES).add(BuiltInRegistries.ITEM.getResourceKey(b.get().asItem()).orElseThrow());
        }
    }
}
