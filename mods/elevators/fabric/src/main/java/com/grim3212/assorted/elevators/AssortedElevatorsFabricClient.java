package com.grim3212.assorted.elevators;

import com.grim3212.assorted.elevators.client.ElevatorsClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedElevatorsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElevatorsClient.init();
    }

}
