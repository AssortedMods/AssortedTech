package com.grim3212.assorted.extruder.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.extruder.Constants;

import java.util.function.Supplier;

public class ExtruderCommonConfig {

    public final Supplier<Double> extruderMoveSpeed;
    public final Supplier<Integer> extruderFuelPerMinedBlock;
    public final Supplier<Integer> extruderFuelPerExtrudedBlock;

    public ExtruderCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        extruderMoveSpeed = builder.defineDouble("extruder.moveSpeed", 0.1D, 0.001D, 1.0D, "How far the extruder moves each tick, in blocks, before its material's speed modifier.");
        extruderFuelPerMinedBlock = builder.defineInteger("extruder.fuelPerMinedBlock", 400, 0, 100000, "The fuel the extruder spends on every block it mines, on top of one per tick while it runs.");
        extruderFuelPerExtrudedBlock = builder.defineInteger("extruder.fuelPerExtrudedBlock", 200, 0, 100000, "The fuel the extruder spends on every block it places behind itself.");

        builder.setup();
    }
}
