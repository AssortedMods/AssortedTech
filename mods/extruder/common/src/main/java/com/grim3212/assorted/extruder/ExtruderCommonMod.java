package com.grim3212.assorted.extruder;

import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.handlers.ExtruderCreativeItems;
import com.grim3212.assorted.extruder.common.inventory.ExtruderMenuTypes;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.extruder.config.ExtruderCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class ExtruderCommonMod {

    public static final ExtruderCommonConfig COMMON_CONFIG = new ExtruderCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "iron_extruder"), 50)
                .manualOrder(100);

        ExtruderItems.init();
        ExtruderEntities.init();
        ExtruderMenuTypes.init();
        ExtruderCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
