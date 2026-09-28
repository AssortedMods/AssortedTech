package com.grim3212.assorted.sensors.common.block.blockentity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SensorsBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<BlockEntityType<SensorBlockEntity>> SENSOR = BLOCK_ENTITIES.register("sensor", () -> Services.PLATFORM.createBlockEntityType(SensorBlockEntity::new, SensorsBlocks.SENSORS.stream().map(x -> x.get()).toArray(Block[]::new)));
    public static final IRegistryObject<BlockEntityType<GpsSensorBlockEntity>> GPS_SENSOR = BLOCK_ENTITIES.register("gps_sensor", () -> Services.PLATFORM.createBlockEntityType(GpsSensorBlockEntity::new, SensorsBlocks.GPS_SENSOR.get(), SensorsBlocks.UPGRADED_GPS_SENSOR.get()));

    public static void init() {
    }
}
