package com.grim3212.assorted.alarm;

import net.fabricmc.api.ModInitializer;

public class AssortedAlarmFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AlarmCommonMod.init();
    }
}
