package com.grim3212.assorted.spikes.gametest;

import com.grim3212.assorted.spikes.api.util.SpikeType;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.spikes.gametest.SpikesTestSupport.spike;

/** Spikes only hurt while powered, as the site says, so an unpowered floor of them is safe to walk. */
final class PowerTests {

    private static final BlockPos SPIKE = new BlockPos(2, 1, 2);

    private PowerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("unpowered_spikes_do_no_harm", PowerTests::unpoweredSpikesDoNoHarm);
    }

    private static void unpoweredSpikesDoNoHarm(GameTestHelper helper) {
        helper.setBlock(SPIKE, spike(SpikeType.DIAMOND).defaultBlockState().setValue(SpikeBlock.FACING, Direction.UP));
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, SPIKE);
        float health = pig.getHealth();
        helper.runAfterDelay(30, () -> {
            helper.assertValueEqual(pig.getHealth(), health, "health of a pig standing in unpowered spikes");
            helper.succeed();
        });
    }
}
