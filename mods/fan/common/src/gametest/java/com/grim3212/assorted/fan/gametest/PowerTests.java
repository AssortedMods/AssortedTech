package com.grim3212.assorted.fan.gametest;

import com.grim3212.assorted.fan.api.util.FanMode;
import com.grim3212.assorted.fan.common.block.FanBlock;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.fan.common.block.blockentity.FanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Powering a fan turns it off, and taking the power away puts back the mode it was set to. */
final class PowerTests {

    private static final BlockPos FAN = new BlockPos(2, 1, 2);

    private PowerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("powered_fan_stops_and_resumes_its_mode", PowerTests::poweredFanStopsAndResumesItsMode);
        out.accept("fan_placed_on_power_starts_off", PowerTests::fanPlacedOnPowerStartsOff);
    }

    private static void poweredFanStopsAndResumesItsMode(GameTestHelper helper) {
        helper.setBlock(FAN, FanBlocks.FAN.get().defaultBlockState().setValue(FanBlock.MODE, FanMode.SUCK));
        helper.getBlockEntity(FAN, FanBlockEntity.class).setMode(FanMode.SUCK);

        helper.setBlock(FAN.east(), Blocks.REDSTONE_BLOCK);
        helper.assertBlockProperty(FAN, FanBlock.MODE, FanMode.OFF);
        helper.setBlock(FAN.east(), Blocks.AIR);
        helper.assertBlockProperty(FAN, FanBlock.MODE, FanMode.SUCK);
        helper.succeed();
    }

    /**
     * A fan set down next to power already on gets no neighbour update, yet stops all the same, and
     * blows once the power goes.
     */
    private static void fanPlacedOnPowerStartsOff(GameTestHelper helper) {
        helper.setBlock(FAN.east(), Blocks.REDSTONE_BLOCK);
        helper.setBlock(FAN, FanBlocks.FAN.get());

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertBlockProperty(FAN, FanBlock.MODE, FanMode.OFF))
                .thenExecute(() -> helper.setBlock(FAN.east(), Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(FAN, FanBlock.MODE, FanMode.BLOW))
                .thenSucceed();
    }
}
