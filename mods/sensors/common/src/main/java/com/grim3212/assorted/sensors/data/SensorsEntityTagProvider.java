package com.grim3212.assorted.sensors.data;

import com.grim3212.assorted.lib.data.LibEntityTagProvider;
import com.grim3212.assorted.sensors.api.SensorsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SensorsEntityTagProvider extends LibEntityTagProvider {

    public SensorsEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void addCommonTags(Function<TagKey<EntityType<?>>, TagAppender<EntityType<?>>> appender) {
        // The library hands out plain TagAppenders, which take ResourceKeys. Wrap them back
        // into something that takes objects so the tag lists below stay readable.
        Function<TagKey<EntityType<?>>, EntityTagger> tagger = (tag) -> new EntityTagger(appender.apply(tag));

        tagger.apply(SensorsTags.Entities.SENSORS_NETHER).add(EntityType.BLAZE, EntityType.GHAST, EntityType.HOGLIN, EntityType.MAGMA_CUBE, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.STRIDER, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN);
        tagger.apply(SensorsTags.Entities.SENSORS_END).add(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON, EntityType.SHULKER);
        tagger.apply(SensorsTags.Entities.SENSORS_ARTHROPODS).add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.BEE, EntityType.ENDERMITE, EntityType.SILVERFISH);
        tagger.apply(SensorsTags.Entities.SENSORS_WATER).add(EntityType.AXOLOTL, EntityType.TURTLE, EntityType.DOLPHIN, EntityType.ELDER_GUARDIAN, EntityType.GLOW_SQUID, EntityType.GUARDIAN, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.SQUID, EntityType.TROPICAL_FISH, EntityType.DROWNED);
        tagger.apply(SensorsTags.Entities.SENSORS_PETS).add(EntityType.CAT, EntityType.DONKEY, EntityType.HORSE, EntityType.LLAMA, EntityType.MULE, EntityType.PARROT, EntityType.SKELETON_HORSE, EntityType.WOLF, EntityType.ZOMBIE_HORSE);
        tagger.apply(SensorsTags.Entities.SENSORS_FLYING).add(EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.CHICKEN, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX, EntityType.WITHER);
    }

    private record EntityTagger(TagAppender<EntityType<?>> appender) {

        EntityTagger add(EntityType<?>... values) {
            for (EntityType<?> value : values) {
                this.appender.add(BuiltInRegistries.ENTITY_TYPE.getResourceKey(value).orElseThrow());
            }

            return this;
        }
    }
}
