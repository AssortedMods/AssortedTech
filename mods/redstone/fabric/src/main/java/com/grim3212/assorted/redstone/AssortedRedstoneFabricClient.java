package com.grim3212.assorted.redstone;

import com.grim3212.assorted.redstone.client.RedstoneClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedRedstoneFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RedstoneClient.init();
    }
}
