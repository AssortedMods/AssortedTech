package com.grim3212.assorted.sensors;

import com.grim3212.assorted.sensors.client.SensorsClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedSensorsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SensorsClient.init();
    }

}
