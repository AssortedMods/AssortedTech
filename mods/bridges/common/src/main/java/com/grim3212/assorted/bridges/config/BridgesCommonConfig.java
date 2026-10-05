package com.grim3212.assorted.bridges.config;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;

import java.util.function.Supplier;

public class BridgesCommonConfig {

    public final Supplier<Integer> bridgeMaxLength;

    public BridgesCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        bridgeMaxLength = builder.defineInteger("bridges.bridgeMaxLength", 128, 1, 1000, "The maximum length that bridges will extend out to in blocks.");

        builder.setup();
    }
}
