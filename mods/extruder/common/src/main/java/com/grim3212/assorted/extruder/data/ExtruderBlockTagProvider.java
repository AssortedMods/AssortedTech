package com.grim3212.assorted.extruder.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.extruder.api.ExtruderTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ExtruderBlockTagProvider extends LibBlockTagProvider {

    public ExtruderBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes objects so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(ExtruderTags.Blocks.EXTRUDER_UNMINEABLE).addTag(LibCommonTags.Blocks.OBSIDIAN);
        // The extruder already refuses anything that cannot be broken (ExtruderEntity#canMine), other
        // mods' blocks included; listing vanilla's here makes the tag show the whole story.
        // Sorted, so the generated file does not churn.
        tagger.apply(ExtruderTags.Blocks.EXTRUDER_UNMINEABLE).add(BuiltInRegistries.BLOCK.stream()
                .filter(block -> block.defaultDestroyTime() < 0.0F)
                .sorted(Comparator.comparing(block -> BuiltInRegistries.BLOCK.getKey(block).toString()))
                .toArray(Block[]::new));
    }

    private record BlockTagger(TagAppender<Block> appender) {

        BlockTagger add(Block... values) {
            for (Block value : values) {
                this.appender.add(BuiltInRegistries.BLOCK.getResourceKey(value).orElseThrow());
            }

            return this;
        }

        BlockTagger addTag(TagKey<Block> tag) {
            this.appender.addTag(tag);
            return this;
        }
    }
}
