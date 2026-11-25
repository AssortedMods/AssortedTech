package com.grim3212.assorted.redstone.client;

import com.grim3212.assorted.redstone.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * The client-only entry point: a second {@code @Mod} for the same mod id, constructed only on the
 * client.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedRedstoneNeoForgeClient {

    public AssortedRedstoneNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        RedstoneClient.init();
    }
}
