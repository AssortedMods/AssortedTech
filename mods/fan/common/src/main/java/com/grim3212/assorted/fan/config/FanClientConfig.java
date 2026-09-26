package com.grim3212.assorted.fan.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.fan.Constants;

import java.util.function.Supplier;

public class FanClientConfig {

    public final Supplier<Boolean> showFanParticles;

    public FanClientConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.CLIENT_ONLY, Constants.MOD_ID + "-client");

        showFanParticles = builder.defineBoolean("fans.showFanParticles", true, "Set this to true if you would like to see particles when a fan is on.");

        builder.setup();
    }
}
