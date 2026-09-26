package com.grim3212.assorted.elevators;

import com.grim3212.assorted.elevators.common.handlers.ElevatorInputHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AssortedElevatorsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ElevatorsCommonMod.init();

        ServerTickEvents.END_LEVEL_TICK.register(ElevatorInputHandler::tick);
    }
}
