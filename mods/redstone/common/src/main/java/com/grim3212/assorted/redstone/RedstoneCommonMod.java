package com.grim3212.assorted.redstone;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import com.grim3212.assorted.redstone.common.handlers.RedstoneCreativeItems;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.resources.Identifier;

public class RedstoneCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "flip_flop_torch"), 100)
                .manualOrder(100);

        RedstoneBlocks.init();
        RedstoneItems.init();
        RedstoneCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
