package com.grim3212.assorted.fan.client;

import com.grim3212.assorted.fan.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * The client-only entry point: a second {@code @Mod} for the same mod id, constructed only on the
 * client, where it registers the air particle the fan blows.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedFanNeoForgeClient {

    public AssortedFanNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        FanClient.init();
    }
}
