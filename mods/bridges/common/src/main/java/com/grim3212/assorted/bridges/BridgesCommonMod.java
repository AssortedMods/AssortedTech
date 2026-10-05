package com.grim3212.assorted.bridges;

import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.bridges.common.block.blockentity.BridgesBlockEntityTypes;
import com.grim3212.assorted.bridges.common.handlers.BridgesCreativeItems;
import com.grim3212.assorted.bridges.config.BridgesCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class BridgesCommonMod {

    public static final BridgesCommonConfig COMMON_CONFIG = new BridgesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bridge_control_laser"), 90)
                .manualOrder(100);

        BridgesBlocks.init();
        BridgesBlockEntityTypes.init();
        BridgesCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
