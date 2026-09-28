package com.grim3212.assorted.sensors.common.item;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.sensors.Constants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class SensorsDataComponents {

    public static final RegistryProvider<DataComponentType<?>> DATA_COMPONENTS = RegistryProvider.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<DataComponentType<GpsTarget>> GPS_TARGET = DATA_COMPONENTS.register("gps_target",
            () -> new DataComponentType.Builder<GpsTarget>().persistent(GpsTarget.CODEC).networkSynchronized(GpsTarget.STREAM_CODEC).build());

    public static void init() {
        Services.PLATFORM.showComponentTooltip(GPS_TARGET);
    }
}
