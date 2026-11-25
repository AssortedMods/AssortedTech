package com.grim3212.assorted.spikes;

import com.grim3212.assorted.spikes.client.SpikesClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedSpikesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SpikesClient.init();
    }
}
