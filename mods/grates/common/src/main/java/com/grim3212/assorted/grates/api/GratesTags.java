package com.grim3212.assorted.grates.api;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class GratesTags {

    public static class Blocks {

        public static final TagKey<Block> ITEM_GRATES = gratesTag("item_grates");

        private static TagKey<Block> gratesTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> ITEM_GRATES = gratesTag("item_grates");

        public static final TagKey<Item> INGOTS_TIN = commonTag("ingots/tin");
        public static final TagKey<Item> INGOTS_SILVER = commonTag("ingots/silver");
        public static final TagKey<Item> INGOTS_ALUMINUM = commonTag("ingots/aluminum");
        public static final TagKey<Item> INGOTS_NICKEL = commonTag("ingots/nickel");
        public static final TagKey<Item> INGOTS_PLATINUM = commonTag("ingots/platinum");
        public static final TagKey<Item> INGOTS_LEAD = commonTag("ingots/lead");
        public static final TagKey<Item> INGOTS_BRONZE = commonTag("ingots/bronze");
        public static final TagKey<Item> INGOTS_ELECTRUM = commonTag("ingots/electrum");
        public static final TagKey<Item> INGOTS_INVAR = commonTag("ingots/invar");
        public static final TagKey<Item> INGOTS_STEEL = commonTag("ingots/steel");

        private static TagKey<Item> gratesTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
