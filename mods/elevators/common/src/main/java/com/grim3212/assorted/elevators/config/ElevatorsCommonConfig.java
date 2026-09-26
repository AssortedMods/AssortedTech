package com.grim3212.assorted.elevators.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.elevators.Constants;

import java.util.function.Supplier;

public class ElevatorsCommonConfig {

    public final Supplier<Double> elevatorSpeed;
    public final Supplier<Integer> elevatorMaxTravel;
    public final Supplier<Integer> instantElevatorRange;

    public ElevatorsCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        elevatorSpeed = builder.defineDouble("elevators.elevatorSpeed", 0.2D, 0.01D, 0.4D, "How far the elevator moves each tick, in blocks.");
        elevatorMaxTravel = builder.defineInteger("elevators.elevatorMaxTravel", 64, 1, 512, "The furthest the elevator travels in one trip, in blocks, when its shaft goes on further than that.");
        instantElevatorRange = builder.defineInteger("elevators.instantElevatorRange", 64, 1, 512, "How far above or below an instant elevator, in blocks, the next one can be and still be reached.");

        builder.setup();
    }
}
