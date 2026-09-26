package com.grim3212.assorted.spikes.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.spikes.Constants;

import java.util.function.Supplier;

public class SpikesCommonConfig {

    public final Supplier<Boolean> hideUncraftableItems;

    public SpikesCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        hideUncraftableItems = builder.defineBoolean("general.hideUncraftableItems", false, "For any item that is unobtainable (like missing materials from other mods) hide it from the creative menu / JEI.");

        builder.setup();
    }
}
