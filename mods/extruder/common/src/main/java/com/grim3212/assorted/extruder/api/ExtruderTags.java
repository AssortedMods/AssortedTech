package com.grim3212.assorted.extruder.api;

import com.grim3212.assorted.extruder.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ExtruderTags {

    public static class Blocks {

        /** What the extruder stops at rather than mines; unbreakable blocks always stop it. */
        public static final TagKey<Block> EXTRUDER_UNMINEABLE = extruderTag("extruder_unmineable");

        private static TagKey<Block> extruderTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {

        public static final int EXTRUDER_LEVELS = 5;

        /** Every extruder of one level, any of which crafts into one of the next. */
        public static TagKey<Item> extruders(int level) {
            return extruderTag("extruders/level_" + level);
        }

        private static TagKey<Item> extruderTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
