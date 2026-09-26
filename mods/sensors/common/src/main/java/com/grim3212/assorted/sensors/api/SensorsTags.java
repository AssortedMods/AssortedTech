package com.grim3212.assorted.sensors.api;

import com.grim3212.assorted.sensors.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SensorsTags {

    public static class Blocks {

        public static final TagKey<Block> SENSORS = tag("sensors");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> SENSORS = tag("sensors");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> SENSORS_NETHER = tag("sensors/nether");
        public static final TagKey<EntityType<?>> SENSORS_END = tag("sensors/end");
        public static final TagKey<EntityType<?>> SENSORS_PETS = tag("sensors/pets");
        public static final TagKey<EntityType<?>> SENSORS_WATER = tag("sensors/water");
        public static final TagKey<EntityType<?>> SENSORS_FLYING = tag("sensors/flying");
        public static final TagKey<EntityType<?>> SENSORS_ARTHROPODS = tag("sensors/arthropods");

        public static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
