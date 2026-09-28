package com.grim3212.assorted.sensors;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorsBlockEntityTypes;
import com.grim3212.assorted.sensors.common.handlers.SensorsCreativeItems;
import com.grim3212.assorted.sensors.common.inventory.SensorsMenuTypes;
import com.grim3212.assorted.sensors.common.item.SensorsDataComponents;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
import com.grim3212.assorted.sensors.common.network.SensorsPackets;
import com.grim3212.assorted.sensors.config.SensorsCommonConfig;
import net.minecraft.resources.Identifier;

public class SensorsCommonMod {

    public static final SensorsCommonConfig COMMON_CONFIG = new SensorsCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "gold_sensor"), 40)
                .manualOrder(100);

        SensorsDataComponents.init();
        SensorsBlocks.init();
        SensorsBlockEntityTypes.init();
        SensorsItems.init();
        SensorsMenuTypes.init();
        SensorsPackets.init();
        SensorsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
