package com.grim3212.assorted.grates;

import com.grim3212.assorted.grates.client.GratesClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedGratesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GratesClient.init();
    }
}
