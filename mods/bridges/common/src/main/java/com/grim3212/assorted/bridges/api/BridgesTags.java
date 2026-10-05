package com.grim3212.assorted.bridges.api;

import com.grim3212.assorted.bridges.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BridgesTags {

    public static class Blocks {

        public static final TagKey<Block> LASER_BREAKABLES = bridgesTag("laser_breakables");

        private static TagKey<Block> bridgesTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {

        /** Boots that keep their wearer out of a gravity bridge's push, Assorted Gravity's gravity boots among them. */
        public static final TagKey<Item> GRAVITY_BRIDGE_IMMUNE = bridgesTag("gravity_bridge_immune");

        private static TagKey<Item> bridgesTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
