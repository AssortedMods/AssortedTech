package com.grim3212.assorted.fan;

import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.fan.common.block.blockentity.FanBlockEntityTypes;
import com.grim3212.assorted.fan.common.handlers.FanCreativeItems;
import com.grim3212.assorted.fan.common.network.FanPackets;
import com.grim3212.assorted.fan.common.particle.FanParticleTypes;
import com.grim3212.assorted.fan.config.FanCommonConfig;
import com.grim3212.assorted.lib.migration.MovedIds;

public class FanCommonMod {

    public static final FanCommonConfig COMMON_CONFIG = new FanCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        FanBlocks.init();
        FanBlockEntityTypes.init();
        FanParticleTypes.init();
        FanPackets.init();
        FanCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
