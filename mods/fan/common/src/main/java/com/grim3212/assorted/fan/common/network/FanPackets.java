package com.grim3212.assorted.fan.common.network;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import net.minecraft.resources.Identifier;

public class FanPackets {

    public static void init() {
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("fan_open"), FanOpenPacket.class, FanOpenPacket::encode, FanOpenPacket::decode, FanOpenPacket::handle, INetworkHelper.MessageBoundSide.CLIENT));
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("fan_update"), FanUpdatePacket.class, FanUpdatePacket::encode, FanUpdatePacket::decode, FanUpdatePacket::handle, INetworkHelper.MessageBoundSide.SERVER));
    }

    private static Identifier resource(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

}
