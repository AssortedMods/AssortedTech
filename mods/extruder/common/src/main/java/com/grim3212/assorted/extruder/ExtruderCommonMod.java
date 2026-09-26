package com.grim3212.assorted.extruder;

import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.handlers.ExtruderCreativeItems;
import com.grim3212.assorted.extruder.common.inventory.ExtruderMenuTypes;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.extruder.config.ExtruderCommonConfig;
import com.grim3212.assorted.lib.migration.MovedIds;

public class ExtruderCommonMod {

    public static final ExtruderCommonConfig COMMON_CONFIG = new ExtruderCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        ExtruderItems.init();
        ExtruderEntities.init();
        ExtruderMenuTypes.init();
        ExtruderCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
