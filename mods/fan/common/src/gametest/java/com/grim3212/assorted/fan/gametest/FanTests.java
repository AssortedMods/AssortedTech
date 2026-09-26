package com.grim3212.assorted.fan.gametest;

import com.grim3212.assorted.fan.api.util.FanMode;
import com.grim3212.assorted.fan.common.block.FanBlock;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.fan.common.block.blockentity.FanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.afterReload;

/**
 * The fan pushing and pulling entities, and its settings across a reload.
 */
final class FanTests {

    private FanTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("block_entity_data_survives_reload", FanTests::blockEntityDataSurvivesReload);
        out.accept("fan_blows_and_sucks", FanTests::fanBlowsAndSucks);
    }

    /**
     * The fan's range and mode survive the {@code saveWithFullMetadata} / {@code loadStatic} round trip a chunk
     * uses. A mismatched {@code ValueOutput} / {@code ValueInput} key or a wrong default is silent.
     */
    private static void blockEntityDataSurvivesReload(GameTestHelper helper) {
        BlockPos fan = new BlockPos(7, 1, 1);
        helper.setBlock(fan, FanBlocks.FAN.get().defaultBlockState().setValue(FanBlock.FACING, Direction.EAST));
        FanBlockEntity fanEntity = helper.getBlockEntity(fan, FanBlockEntity.class);
        fanEntity.setRange(7);
        fanEntity.setMode(FanMode.SUCK);

        helper.assertValueEqual(afterReload(helper, fan, FanBlockEntity.class).getRange(), 7,
                "fan range after a reload");
        helper.assertValueEqual(afterReload(helper, fan, FanBlockEntity.class).getMode(), FanMode.SUCK,
                "fan mode after a reload");

        helper.succeed();
    }

    /**
     * A blowing fan pushes entities away from its face and a sucking one pulls them in. They run in
     * separate lanes because at range 4 their volumes would overlap.
     */
    private static void fanBlowsAndSucks(GameTestHelper helper) {
        BlockPos blower = new BlockPos(1, 1, 1);
        BlockPos sucker = new BlockPos(7, 1, 6);

        helper.setBlock(blower, FanBlocks.FAN.get().defaultBlockState()
                .setValue(FanBlock.FACING, Direction.EAST).setValue(FanBlock.MODE, FanMode.BLOW));
        FanBlockEntity blowing = helper.getBlockEntity(blower, FanBlockEntity.class);
        blowing.setMode(FanMode.BLOW);
        blowing.setRange(4);

        helper.setBlock(sucker, FanBlocks.FAN.get().defaultBlockState()
                .setValue(FanBlock.FACING, Direction.WEST).setValue(FanBlock.MODE, FanMode.SUCK));
        FanBlockEntity sucking = helper.getBlockEntity(sucker, FanBlockEntity.class);
        sucking.setMode(FanMode.SUCK);
        sucking.setRange(4);

        Pig blown = helper.spawnWithNoFreeWill(EntityTypes.PIG, new BlockPos(2, 1, 1));
        Pig drawn = helper.spawnWithNoFreeWill(EntityTypes.PIG, new BlockPos(4, 1, 6));

        Vec3 blowerCorner = Vec3.atLowerCornerOf(helper.absolutePos(blower));
        Vec3 suckerCorner = Vec3.atLowerCornerOf(helper.absolutePos(sucker));
        double blownStart = blown.position().distanceTo(blowerCorner);
        double drawnStart = drawn.position().distanceTo(suckerCorner);
        double[] reach = {blownStart, drawnStart};

        helper.startSequence()
                .thenExecuteFor(40, () -> {
                    reach[0] = Math.max(reach[0], blown.position().distanceTo(blowerCorner));
                    reach[1] = Math.min(reach[1], drawn.position().distanceTo(suckerCorner));
                })
                .thenExecute(() -> {
                    helper.assertTrue(reach[0] > blownStart + 0.5D,
                            "a blowing fan did not push an entity away - it went from " + blownStart + " to " + reach[0]);
                    helper.assertTrue(reach[1] < drawnStart - 0.5D,
                            "a sucking fan did not pull an entity in - it went from " + drawnStart + " to " + reach[1]);
                })
                .thenSucceed();
    }
}
