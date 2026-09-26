package com.grim3212.assorted.alarm;

import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntityTypes;
import com.grim3212.assorted.alarm.common.handlers.AlarmCreativeItems;
import com.grim3212.assorted.alarm.common.network.AlarmPackets;
import com.grim3212.assorted.alarm.common.sounds.AlarmSounds;
import com.grim3212.assorted.lib.migration.MovedIds;

public class AlarmCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        AlarmBlocks.init();
        AlarmBlockEntityTypes.init();
        AlarmSounds.init();
        AlarmPackets.init();
        AlarmCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
