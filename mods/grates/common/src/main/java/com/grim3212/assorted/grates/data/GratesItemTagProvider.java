package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.api.GratesTags;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
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

public class GratesItemTagProvider extends LibItemTagProvider {

    public GratesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // TagAppender only accepts ResourceKeys, so each grate goes in by its key.
        GratesBlocks.allItemGrates().forEach(grate ->
                appender.apply(GratesTags.Items.ITEM_GRATES).add(BuiltInRegistries.ITEM.getResourceKey(grate.get().asItem()).orElseThrow()));
    }
}
