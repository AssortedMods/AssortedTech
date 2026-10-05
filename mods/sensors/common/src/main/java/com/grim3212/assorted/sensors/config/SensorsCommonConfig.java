package com.grim3212.assorted.sensors.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.sensors.Constants;

import java.util.function.Supplier;

public class SensorsCommonConfig {

    public final Supplier<Integer> gpsSensorRange;
    public final Supplier<Integer> upgradedGpsSensorRange;
    public final Supplier<Integer> upgradedGpsSensorMaxRadius;

    public SensorsCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        gpsSensorRange = builder.defineInteger("gps.gpsSensorRange", 7, 1, 64, "How far from a GPS sensor, in blocks, the position it watches can be.");
        upgradedGpsSensorRange = builder.defineInteger("gps.upgradedGpsSensorRange", 11, 1, 64, "How far from an upgraded GPS sensor, in blocks, the position it watches can be.");
        upgradedGpsSensorMaxRadius = builder.defineInteger("gps.upgradedGpsSensorMaxRadius", 4, 0, 16, "How far around its position, in blocks, an upgraded GPS sensor can be set to watch.");

        builder.setup();
    }
}
