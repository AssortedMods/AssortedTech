package com.grim3212.assorted.gravity;

import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.block.blockentity.GravityBlockEntityTypes;
import com.grim3212.assorted.gravity.common.handlers.GravityCreativeItems;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import com.grim3212.assorted.gravity.config.GravityCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class GravityCommonMod {

    public static final GravityCommonConfig COMMON_CONFIG = new GravityCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "attractor"), 70)
                .manualOrder(100);

        GravityBlocks.init();
        GravityBlockEntityTypes.init();
        GravityItems.init();
        GravityCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
