package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.bridges.api.BridgesTags;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BridgesBlockTagProvider extends LibBlockTagProvider {

    public BridgesBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The appender takes ResourceKeys; this takes blocks, so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(BridgesTags.Blocks.LASER_BREAKABLES).add(Blocks.WATER, Blocks.LAVA, Blocks.ICE, Blocks.SUGAR_CANE, Blocks.SNOW, Blocks.POWDER_SNOW, Blocks.WHEAT, Blocks.POTATOES, Blocks.CARROTS, Blocks.BEETROOTS);

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BridgesBlocks.BRIDGE_CONTROL_ACCEL.get(), BridgesBlocks.BRIDGE_CONTROL_DEATH.get(), BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get(), BridgesBlocks.BRIDGE_CONTROL_LASER.get(), BridgesBlocks.BRIDGE_CONTROL_TRICK.get());
    }

    private record BlockTagger(TagAppender<Block> appender) {

        BlockTagger add(Block... values) {
            for (Block value : values) {
                this.appender.add(BuiltInRegistries.BLOCK.getResourceKey(value).orElseThrow());
            }

            return this;
        }
    }
}
