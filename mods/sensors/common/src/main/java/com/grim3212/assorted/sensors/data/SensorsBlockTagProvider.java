package com.grim3212.assorted.sensors.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.api.SensorsTags;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SensorsBlockTagProvider extends LibBlockTagProvider {

    public SensorsBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes objects so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        for (IRegistryObject<SensorBlock> b : SensorsBlocks.SENSORS) {
            tagger.apply(SensorsTags.Blocks.SENSORS).add(b.get());
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(b.get());
        }

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(SensorsBlocks.GPS_SENSOR.get(), SensorsBlocks.UPGRADED_GPS_SENSOR.get());
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
