package com.grim3212.assorted.alarm.gametest;

import com.grim3212.assorted.alarm.common.block.AlarmBlock;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** The alarm goes off when the block it hangs on is powered and resets once the power goes, so it sounds again next time. */
final class PowerTests {

    private static final BlockPos WALL = new BlockPos(2, 2, 2);

    private PowerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("alarm_sounds_while_its_wall_is_powered", PowerTests::alarmSoundsWhileItsWallIsPowered);
    }

    private static void alarmSoundsWhileItsWallIsPowered(GameTestHelper helper) {
        BlockPos alarm = WALL.south();
        helper.setBlock(WALL, Blocks.STONE);
        helper.setBlock(alarm, AlarmBlocks.ALARM.get().defaultBlockState().setValue(AlarmBlock.FACING, Direction.SOUTH));
        helper.assertBlockProperty(alarm, AlarmBlock.POWERED, false);

        helper.setBlock(WALL.west(), Blocks.REDSTONE_BLOCK);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertBlockProperty(alarm, AlarmBlock.POWERED, true))
                .thenExecute(() -> helper.setBlock(WALL.west(), Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(alarm, AlarmBlock.POWERED, false))
                .thenSucceed();
    }
}
