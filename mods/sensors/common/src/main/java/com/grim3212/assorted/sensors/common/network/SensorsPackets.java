package com.grim3212.assorted.sensors.common.network;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import com.grim3212.assorted.sensors.Constants;
import net.minecraft.resources.ResourceLocation;

public class SensorsPackets {

    public static void init() {
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("gps_sensor_filter"), GpsSensorFilterPacket.class, GpsSensorFilterPacket::encode, GpsSensorFilterPacket::decode, GpsSensorFilterPacket::handle, INetworkHelper.MessageBoundSide.SERVER));
    }

    private static ResourceLocation resource(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

}
