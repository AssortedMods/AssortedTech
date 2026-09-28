package com.grim3212.assorted.spikes;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import com.grim3212.assorted.spikes.common.handlers.SpikesCreativeItems;
import com.grim3212.assorted.spikes.common.sounds.SpikesSounds;
import com.grim3212.assorted.spikes.config.SpikesCommonConfig;
import net.minecraft.resources.Identifier;

public class SpikesCommonMod {

    public static final SpikesCommonConfig COMMON_CONFIG = new SpikesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "iron_spike"), 30)
                .manualOrder(100);

        SpikesBlocks.init();
        SpikesSounds.init();
        SpikesCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
