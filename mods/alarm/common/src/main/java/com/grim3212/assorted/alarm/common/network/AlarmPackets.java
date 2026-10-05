package com.grim3212.assorted.alarm.common.network;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.platform.services.INetworkHelper;
import net.minecraft.resources.Identifier;

public class AlarmPackets {

    public static void init() {
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("alarm_open"), AlarmOpenPacket.class, AlarmOpenPacket::encode, AlarmOpenPacket::decode, AlarmOpenPacket::handle, INetworkHelper.MessageBoundSide.CLIENT));
        Services.NETWORK.register(new INetworkHelper.MessageHandler<>(resource("alarm_update"), AlarmUpdatePacket.class, AlarmUpdatePacket::encode, AlarmUpdatePacket::decode, AlarmUpdatePacket::handle, INetworkHelper.MessageBoundSide.SERVER));
    }

    private static Identifier resource(String name) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
    }

}
