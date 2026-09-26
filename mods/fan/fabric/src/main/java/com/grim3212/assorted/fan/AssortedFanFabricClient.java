package com.grim3212.assorted.fan;

import com.grim3212.assorted.fan.client.FanClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedFanFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FanClient.init();
    }

}
