package com.grim3212.assorted.sensors.gametest;

import com.grim3212.assorted.sensors.api.util.GpsSensorMode;
import com.grim3212.assorted.sensors.api.util.SensorType;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import com.grim3212.assorted.sensors.common.block.blockentity.GpsSensorBlockEntity;
import com.grim3212.assorted.sensors.common.block.blockentity.SensorBlockEntity;
import com.grim3212.assorted.sensors.common.item.GpsTarget;
import com.grim3212.assorted.sensors.common.item.SensorsDataComponents;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.sensors.gametest.SensorsTestSupport.sensorOf;

/**
 * The bounds a client culls the range box and the GPS outline by. Drawing needs a client; these
 * check that the bounds reach as far as what is drawn, so neither vanishes while still in view.
 */
final class RenderBoundsTests {

    private RenderBoundsTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("sensor_render_bounds_cover_its_range", RenderBoundsTests::sensorRenderBoundsCoverItsRange);
        out.accept("gps_sensor_render_bounds_cover_its_target", RenderBoundsTests::gpsSensorRenderBoundsCoverItsTarget);
    }

    /** Facing every way at the longest range, the bounds hold the sensor and the far end of its box. */
    private static void sensorRenderBoundsCoverItsRange(GameTestHelper helper) {
        BlockPos sensorPos = new BlockPos(4, 2, 4);
        for (Direction dir : Direction.values()) {
            helper.setBlock(sensorPos, sensorOf(SensorType.WOOD).defaultBlockState().setValue(SensorBlock.FACING, dir));
            SensorBlockEntity sensor = helper.getBlockEntity(sensorPos, SensorBlockEntity.class);
            // Range 1 steps back round to the longest, 15.
            sensor.reverseCycleRange();
            BlockPos absolute = helper.absolutePos(sensorPos);
            AABB bounds = sensor.rangeBounds();

            helper.assertTrue(bounds.contains(Vec3.atCenterOf(absolute)),
                    "the bounds of a sensor facing " + dir + " leave out the sensor: " + bounds);
            helper.assertTrue(bounds.contains(Vec3.atCenterOf(absolute.relative(dir, sensor.getRange()))),
                    "the bounds of a sensor facing " + dir + " stop short of its range of " + sensor.getRange() + ": " + bounds);
        }
        helper.succeed();
    }

    /** Plain and upgraded, the bounds hold the sensor and every corner of the box it watches. */
    private static void gpsSensorRenderBoundsCoverItsTarget(GameTestHelper helper) {
        checkGpsBounds(helper, new BlockPos(1, 1, 1), SensorsBlocks.GPS_SENSOR.get().defaultBlockState(), 0);
        checkGpsBounds(helper, new BlockPos(1, 1, 7), SensorsBlocks.UPGRADED_GPS_SENSOR.get().defaultBlockState(), 1);
        helper.succeed();
    }

    private static void checkGpsBounds(GameTestHelper helper, BlockPos sensorPos, BlockState state, int radius) {
        helper.setBlock(sensorPos, state);
        GpsSensorBlockEntity sensor = helper.getBlockEntity(sensorPos, GpsSensorBlockEntity.class);
        ItemStack gps = new ItemStack(SensorsItems.GPS.get());
        gps.set(SensorsDataComponents.GPS_TARGET.get(), new GpsTarget(GlobalPos.of(helper.getLevel().dimension(), helper.absolutePos(sensorPos.offset(4, 0, 0)))));
        sensor.getGpsContainer().setItem(0, gps);
        sensor.configure(GpsSensorMode.MOBS, "", ItemStack.EMPTY, radius, true);

        AABB bounds = sensor.outlineBounds();
        AABB watched = sensor.senseBox();
        helper.assertTrue(bounds.contains(Vec3.atCenterOf(helper.absolutePos(sensorPos))),
                "the bounds of " + state.getBlock() + " leave out the sensor: " + bounds);
        helper.assertTrue(bounds.minX <= watched.minX && bounds.minY <= watched.minY && bounds.minZ <= watched.minZ
                        && bounds.maxX >= watched.maxX && bounds.maxY >= watched.maxY && bounds.maxZ >= watched.maxZ,
                "the bounds of " + state.getBlock() + " " + bounds + " leave out part of the box it watches " + watched);
    }
}
