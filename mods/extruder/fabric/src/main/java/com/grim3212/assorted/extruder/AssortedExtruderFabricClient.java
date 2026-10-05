package com.grim3212.assorted.extruder;

import com.grim3212.assorted.extruder.client.ExtruderClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedExtruderFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExtruderClient.init();
    }

}
