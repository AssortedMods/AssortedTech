package com.grim3212.assorted.gravity.config;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class GravityCommonConfig {

    public final Supplier<Double> attractRepulseSpeed;
    public final Supplier<Double> attractRepulseModSpeed;
    public final Supplier<Double> gravitorSpeed;

    public final Supplier<Integer> gravityMaxRange;

    public GravityCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        gravityMaxRange = builder.defineInteger("gravity.gravityMaxRange", 15, 1, 1000, "The maximum distance at which the range for gravity blocks can be set.");
        attractRepulseSpeed = builder.defineDouble("gravity.attractRepulseSpeed", 0.13D, 0.001D, 1000D, "The base speed at which the attractor and repulsor moves entities.");
        attractRepulseModSpeed = builder.defineDouble("gravity.attractRepulseModSpeed", 0.065D, 0.001D, 1000D, "The modifier speed at which the attractor and repulsor moves entities will be added onto the base speed divided by the maxRange-fanRange.");
        gravitorSpeed = builder.defineDouble("gravity.gravitorSpeed", 0.1D, 0.001D, 10D, "The speed at which the gravitor blocks will move entities up.");

        builder.setup();
    }
}
