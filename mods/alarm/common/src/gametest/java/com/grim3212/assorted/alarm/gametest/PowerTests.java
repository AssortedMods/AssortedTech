package com.grim3212.assorted.alarm.gametest;

import com.grim3212.assorted.alarm.common.block.AlarmBlock;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntity;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.alarm.gametest.AlarmTestSupport.listenerAt;
import static com.grim3212.assorted.alarm.gametest.AlarmTestSupport.soundsHeard;

/** The alarm goes off when the block it hangs on is powered and resets once the power goes, so it sounds again next time. */
final class PowerTests {

    private static final BlockPos WALL = new BlockPos(2, 2, 2);

    private PowerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("alarm_sounds_while_its_wall_is_powered", PowerTests::alarmSoundsWhileItsWallIsPowered);
    }

    /**
     * Powering the wall plays the alarm's chosen sound to a player nearby, once; losing the power
     * plays nothing, and powering it again sounds it again.
     */
    private static void alarmSoundsWhileItsWallIsPowered(GameTestHelper helper) {
        BlockPos alarm = WALL.south();
        helper.setBlock(WALL, Blocks.STONE);
        helper.setBlock(alarm, AlarmBlocks.ALARM.get().defaultBlockState().setValue(AlarmBlock.FACING, Direction.SOUTH));
        // Not the first sound, so the alarm is heard playing the one it is set to.
        helper.<AlarmBlockEntity>getBlockEntity(alarm).setAlarmType(3);
        SoundEvent expected = AlarmBlockEntity.getSound(3).get();
        EmbeddedChannel listener = listenerAt(helper, alarm.south(2));
        helper.assertBlockProperty(alarm, AlarmBlock.POWERED, false);

        helper.setBlock(WALL.west(), Blocks.REDSTONE_BLOCK);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertBlockProperty(alarm, AlarmBlock.POWERED, true))
                .thenExecute(() -> assertAlarmSounds(helper, listener, alarm, expected, 1, "powering its wall"))
                .thenExecute(() -> helper.setBlock(WALL.west(), Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(alarm, AlarmBlock.POWERED, false))
                .thenExecute(() -> assertAlarmSounds(helper, listener, alarm, expected, 0, "taking the power away"))
                .thenExecute(() -> helper.setBlock(WALL.west(), Blocks.REDSTONE_BLOCK))
                .thenWaitUntil(() -> helper.assertBlockProperty(alarm, AlarmBlock.POWERED, true))
                .thenExecute(() -> assertAlarmSounds(helper, listener, alarm, expected, 1, "powering its wall again"))
                .thenSucceed();
    }

    private static void assertAlarmSounds(GameTestHelper helper, EmbeddedChannel listener, BlockPos alarm, SoundEvent expected, int times, String after) {
        Vec3 at = helper.absoluteVec(Vec3.atCenterOf(alarm));
        List<ClientboundSoundPacket> heard = soundsHeard(listener);
        long played = heard.stream()
                .filter(sound -> sound.getSound().value() == expected && at.closerThan(new Vec3(sound.getX(), sound.getY(), sound.getZ()), 1.0D))
                .count();
        helper.assertTrue(played == times, "after " + after + ", the alarm sound played " + played + " times rather than " + times
                + "; heard " + heard.stream().map(sound -> sound.getSound().value().getLocation().toString()).toList());
    }
}
