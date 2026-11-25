package com.grim3212.assorted.sensors.client;

import com.grim3212.assorted.sensors.client.blockentity.GpsSensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.client.blockentity.SensorBlockEntityRenderer;
import com.grim3212.assorted.sensors.common.block.blockentity.GpsSensorBlockEntity;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

/**
 * NeoForge culls every block entity renderer by its render bounding box, the block's own by default,
 * so a range box would vanish whenever its sensor is out of view. These give the whole box instead.
 */
final class NeoForgeSensorRenderers {

    private NeoForgeSensorRenderers() {
    }

    static final class Sensor extends SensorBlockEntityRenderer {

        Sensor(BlockEntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public AABB getRenderBoundingBox(SensorBlockEntity entity) {
            return entity.rangeBounds();
        }
    }

    static final class GpsSensor extends GpsSensorBlockEntityRenderer {

        GpsSensor(BlockEntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public AABB getRenderBoundingBox(GpsSensorBlockEntity entity) {
            return entity.outlineBounds();
        }
    }
}
