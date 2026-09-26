package com.grim3212.assorted.sensors.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.sensors.client.blockentity.GpsSensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.client.blockentity.SensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.client.screen.GpsSensorScreen;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorsBlockEntityTypes;
import com.grim3212.assorted.sensors.common.inventory.SensorsMenuTypes;

public class SensorsClient {

    public static void init() {
        ClientServices.CLIENT.registerBlockEntityRenderer(SensorsBlockEntityTypes.SENSOR, SensorBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(SensorsBlockEntityTypes.GPS_SENSOR, GpsSensorBlockEntityRenderer::new);

        ClientServices.CLIENT.registerScreen(SensorsMenuTypes.GPS_SENSOR::get, GpsSensorScreen::new);
    }

}
