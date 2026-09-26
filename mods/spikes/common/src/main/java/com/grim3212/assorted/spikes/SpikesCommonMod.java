package com.grim3212.assorted.spikes;

import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import com.grim3212.assorted.spikes.common.handlers.SpikesCreativeItems;
import com.grim3212.assorted.spikes.common.sounds.SpikesSounds;
import com.grim3212.assorted.spikes.config.SpikesCommonConfig;

public class SpikesCommonMod {

    public static final SpikesCommonConfig COMMON_CONFIG = new SpikesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        SpikesBlocks.init();
        SpikesSounds.init();
        SpikesCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
