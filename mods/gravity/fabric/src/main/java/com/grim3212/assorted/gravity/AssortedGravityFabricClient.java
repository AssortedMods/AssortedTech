package com.grim3212.assorted.gravity;

import com.grim3212.assorted.gravity.client.GravityClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedGravityFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GravityClient.init();
    }

}
