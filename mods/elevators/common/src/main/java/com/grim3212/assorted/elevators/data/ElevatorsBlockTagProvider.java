package com.grim3212.assorted.elevators.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ElevatorsBlockTagProvider extends LibBlockTagProvider {

    public ElevatorsBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes objects so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(ElevatorsBlocks.ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), ElevatorsBlocks.ELEVATOR_LANDING.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get(), ElevatorsBlocks.INSTANT_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get());
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
