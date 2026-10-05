package com.grim3212.assorted.elevators;

import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.blockentity.ElevatorsBlockEntityTypes;
import com.grim3212.assorted.elevators.common.entity.ElevatorsEntities;
import com.grim3212.assorted.elevators.common.handlers.ElevatorsCreativeItems;
import com.grim3212.assorted.elevators.config.ElevatorsCommonConfig;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class ElevatorsCommonMod {

    public static final ElevatorsCommonConfig COMMON_CONFIG = new ElevatorsCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "elevator"), 80)
                .manualOrder(100);

        ElevatorsBlocks.init();
        ElevatorsBlockEntityTypes.init();
        ElevatorsEntities.init();
        ElevatorsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
