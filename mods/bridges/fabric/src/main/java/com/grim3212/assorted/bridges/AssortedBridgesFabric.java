package com.grim3212.assorted.bridges;

import net.fabricmc.api.ModInitializer;

public class AssortedBridgesFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BridgesCommonMod.init();
    }
}
