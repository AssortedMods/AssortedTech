package com.grim3212.assorted.extruder.client;

import com.grim3212.assorted.extruder.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * The client-only entry point: a second {@code @Mod} for the same mod id, constructed only on the
 * client, client datagen included.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedExtruderNeoForgeClient {

    public AssortedExtruderNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        ExtruderClient.init();
    }
}
