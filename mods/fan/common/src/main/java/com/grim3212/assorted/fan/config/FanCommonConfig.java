package com.grim3212.assorted.fan.config;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class FanCommonConfig {

    public final Supplier<Double> fanSpeed;
    public final Supplier<Double> fanModSpeed;
    public final Supplier<Integer> fanMaxRange;

    public FanCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        fanSpeed = builder.defineDouble("fans.fanSpeed", 0.13D, 0.001D, 1000D, "The base speed at which the fan blows or sucks entities.");
        fanModSpeed = builder.defineDouble("fans.fanModSpeed", 0.065D, 0.001D, 1000D, "The modifier speed at which the fan blows or sucks entities will be added onto the base speed divided by the maxRange-fanRange.");
        fanMaxRange = builder.defineInteger("fans.fanMaxRange", 32, 1, 1000, "The maximum distance at which the range for fans can be set.");

        builder.setup();
    }
}
