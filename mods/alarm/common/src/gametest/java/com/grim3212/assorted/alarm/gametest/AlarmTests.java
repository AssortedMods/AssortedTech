package com.grim3212.assorted.alarm.gametest;

import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntity;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The alarm and its block entity.
 */
final class AlarmTests {

    private AlarmTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("alarm_has_its_own_block_entity", AlarmTests::alarmHasItsOwnBlockEntity);
    }

    /**
     * The alarm gets an {@link AlarmBlockEntity} whose type is bound to the alarm block. Bound to
     * the wrong block, every alarm block entity is invalid, and nothing catches it at compile time.
     */
    private static void alarmHasItsOwnBlockEntity(GameTestHelper helper) {
        BlockPos alarm = new BlockPos(4, 1, 4);

        helper.setBlock(alarm, AlarmBlocks.ALARM.get().defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));

        // Throws with "wrong_block_entity" if the alarm ever gets anything else.
        helper.getBlockEntity(alarm, AlarmBlockEntity.class);

        helper.assertTrue(AlarmBlockEntityTypes.ALARM.get().isValid(helper.getBlockState(alarm)),
                "the alarm block entity type is not valid for the alarm block");

        helper.succeed();
    }
}
