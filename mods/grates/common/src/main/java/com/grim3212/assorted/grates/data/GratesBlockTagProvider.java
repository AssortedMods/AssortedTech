package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.api.GratesTags;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GratesBlockTagProvider extends LibBlockTagProvider {

    public GratesBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // TagAppender only accepts ResourceKeys, so each grate goes in by its key.
        GratesBlocks.allItemGrates().forEach(grate -> {
            appender.apply(GratesTags.Blocks.ITEM_GRATES).add(BuiltInRegistries.BLOCK.getResourceKey(grate.get()).orElseThrow());
            appender.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(grate.get()).orElseThrow());
        });
    }
}
