package com.grim3212.assorted.sensors;

import net.fabricmc.api.ModInitializer;

public class AssortedSensorsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SensorsCommonMod.init();
    }
}
