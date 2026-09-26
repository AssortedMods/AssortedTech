package com.grim3212.assorted.tech;

import com.grim3212.assorted.tech.common.handlers.ElevatorInputHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AssortedTechFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TechCommonMod.init();

        ServerTickEvents.END_LEVEL_TICK.register(ElevatorInputHandler::tick);
    }
}
