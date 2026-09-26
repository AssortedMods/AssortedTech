package com.grim3212.assorted.redstone;

import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import com.grim3212.assorted.redstone.common.handlers.RedstoneCreativeItems;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;

public class RedstoneCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        RedstoneBlocks.init();
        RedstoneItems.init();
        RedstoneCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
