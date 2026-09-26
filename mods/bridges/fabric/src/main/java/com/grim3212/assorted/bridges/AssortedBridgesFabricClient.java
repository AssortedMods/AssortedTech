package com.grim3212.assorted.bridges;

import com.grim3212.assorted.bridges.client.BridgesClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedBridgesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BridgesClient.init();
    }

}
