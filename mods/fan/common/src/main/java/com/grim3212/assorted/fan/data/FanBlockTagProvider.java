package com.grim3212.assorted.fan.data;

import com.grim3212.assorted.fan.common.block.FanBlocks;
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

public class FanBlockTagProvider extends LibBlockTagProvider {

    public FanBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // TagAppender only accepts ResourceKeys, so the block goes in by its key.
        appender.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(FanBlocks.FAN.get()).orElseThrow());
    }
}
