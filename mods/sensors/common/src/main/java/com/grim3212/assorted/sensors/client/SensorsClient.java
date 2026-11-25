package com.grim3212.assorted.sensors.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.sensors.client.blockentity.GpsSensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.client.blockentity.SensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.client.screen.GpsSensorScreen;
import com.grim3212.assorted.sensors.common.block.blockentity.GpsSensorBlockEntity;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorBlockEntity;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorsBlockEntityTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.grim3212.assorted.sensors.common.inventory.SensorsMenuTypes;

public class SensorsClient {

    public static void init() {
        init(SensorBlockEntityRenderer::new, GpsSensorBlockEntityRenderer::new);
    }

    /** With the loader's own renderers, where a loader needs more of them than common can give. */
    public static void init(BlockEntityRendererProvider<SensorBlockEntity> sensorRenderer, BlockEntityRendererProvider<GpsSensorBlockEntity> gpsSensorRenderer) {
        ClientServices.CLIENT.registerBlockEntityRenderer(SensorsBlockEntityTypes.SENSOR, sensorRenderer);
        ClientServices.CLIENT.registerBlockEntityRenderer(SensorsBlockEntityTypes.GPS_SENSOR, gpsSensorRenderer);

        ClientServices.CLIENT.registerScreen(SensorsMenuTypes.GPS_SENSOR::get, GpsSensorScreen::new);
    }

}
