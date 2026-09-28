package com.grim3212.assorted.grates;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.handlers.GratesCreativeItems;
import com.grim3212.assorted.grates.config.GratesCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.Identifier;

public class GratesCommonMod {

    public static final GratesCommonConfig COMMON_CONFIG = new GratesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "iron_item_grate"), 60)
                .manualOrder(100);

        GratesBlocks.init();
        GratesCreativeItems.init();
    }
}
