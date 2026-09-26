package com.grim3212.assorted.grates;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.handlers.GratesCreativeItems;
import com.grim3212.assorted.grates.config.GratesCommonConfig;

public class GratesCommonMod {

    public static final GratesCommonConfig COMMON_CONFIG = new GratesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        GratesBlocks.init();
        GratesCreativeItems.init();
    }
}
