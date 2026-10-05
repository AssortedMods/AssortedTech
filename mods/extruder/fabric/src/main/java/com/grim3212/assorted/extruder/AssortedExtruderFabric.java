package com.grim3212.assorted.extruder;

import net.fabricmc.api.ModInitializer;

public class AssortedExtruderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ExtruderCommonMod.init();
    }
}
