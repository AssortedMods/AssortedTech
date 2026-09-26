package com.grim3212.assorted.redstone.gametest;

import com.grim3212.assorted.redstone.common.block.FlipFlopTorchBlock;
import com.grim3212.assorted.redstone.common.block.FlipFlopWallTorchBlock;
import com.grim3212.assorted.redstone.common.block.GlowstoneTorchBlock;
import com.grim3212.assorted.redstone.common.block.GlowstoneWallTorchBlock;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.redstone.gametest.RedstoneTestSupport.*;

/**
 * The flip flop and glowstone torches, standing and on a wall.
 */
final class TorchTests {

    private TorchTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("flip_flop_torch_latches", TorchTests::flipFlopTorchLatches);
        out.accept("glowstone_torches_light_with_signal", TorchTests::glowstoneTorchesLightWithSignal);
        out.accept("flip_flop_wall_torch_latches", TorchTests::flipFlopWallTorchLatches);
    }

    /**
     * The flip flop torch toggles once per rising edge and holds while unpowered, which depends on
     * {@code LIT}/{@code PREV_LIT} updating in the right order. It reads the block it stands on, so
     * that is the block that gets powered.
     */
    private static void flipFlopTorchLatches(GameTestHelper helper) {
        BlockPos support = new BlockPos(4, 1, 4);
        BlockPos torch = new BlockPos(4, 2, 4);

        helper.startSequence()
                .thenExecute(() -> {
                    helper.setBlock(support, Blocks.STONE);
                    helper.setBlock(torch, RedstoneBlocks.FLIP_FLOP_TORCH.get());
                })
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, false))
                .thenExecute(() -> helper.setBlock(support, Blocks.REDSTONE_BLOCK))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, true))
                .thenExecute(() -> helper.setBlock(support, Blocks.STONE))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, true))
                .thenExecute(() -> helper.setBlock(support, Blocks.REDSTONE_BLOCK))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, false))
                .thenSucceed();
    }

    /**
     * The standing and wall glowstone torches light on a redstone signal, go dark without one, and
     * report the light level the block claims. A lit torch outreaches the gap between test boxes,
     * so it stays mid-box and is put out before the test ends.
     */
    private static void glowstoneTorchesLightWithSignal(GameTestHelper helper) {
        BlockPos standSupport = new BlockPos(3, 2, 4);
        BlockPos stand = new BlockPos(3, 3, 4);
        BlockPos wallSupport = new BlockPos(6, 3, 4);
        BlockPos wall = new BlockPos(5, 3, 4);

        helper.runBeforeTestEnd(() -> extinguish(helper, stand, wall, standSupport, wallSupport));

        helper.startSequence()
                .thenExecute(() -> {
                    helper.setBlock(standSupport, Blocks.STONE);
                    helper.setBlock(stand, RedstoneBlocks.GLOWSTONE_TORCH.get());
                    helper.setBlock(wallSupport, Blocks.STONE);
                    helper.setBlock(wall, RedstoneBlocks.GLOWSTONE_WALL_TORCH.get().defaultBlockState()
                            .setValue(GlowstoneWallTorchBlock.FACING, Direction.WEST));
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertBlockProperty(stand, GlowstoneTorchBlock.LIT, false);
                    helper.assertBlockProperty(wall, GlowstoneTorchBlock.LIT, false);
                    helper.assertValueEqual(lightEmission(helper, stand), 0, "unlit glowstone torch light level");
                    helper.assertValueEqual(lightEmission(helper, wall), 0, "unlit glowstone wall torch light level");
                    helper.setBlock(standSupport, Blocks.REDSTONE_BLOCK);
                    helper.setBlock(wallSupport, Blocks.REDSTONE_BLOCK);
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertBlockProperty(stand, GlowstoneTorchBlock.LIT, true);
                    helper.assertBlockProperty(wall, GlowstoneTorchBlock.LIT, true);
                    helper.assertValueEqual(lightEmission(helper, stand), 15, "lit glowstone torch light level");
                    helper.assertValueEqual(lightEmission(helper, wall), 15, "lit glowstone wall torch light level");
                    helper.setBlock(standSupport, Blocks.STONE);
                    helper.setBlock(wallSupport, Blocks.STONE);
                })
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertBlockProperty(stand, GlowstoneTorchBlock.LIT, false);
                    helper.assertBlockProperty(wall, GlowstoneTorchBlock.LIT, false);
                })
                .thenSucceed();
    }

    /**
     * The wall flip flop torch latches like the standing one, but off the block it hangs on: a
     * separate {@code hasNeighborSignal} override. Lit torches are handled as in {@link
     * #glowstoneTorchesLightWithSignal}.
     */
    private static void flipFlopWallTorchLatches(GameTestHelper helper) {
        BlockPos support = new BlockPos(3, 3, 4);
        BlockPos torch = new BlockPos(4, 3, 4);

        helper.runBeforeTestEnd(() -> extinguish(helper, torch, support));

        helper.startSequence()
                .thenExecute(() -> {
                    helper.setBlock(support, Blocks.STONE);
                    helper.setBlock(torch, RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get().defaultBlockState()
                            .setValue(FlipFlopWallTorchBlock.FACING, Direction.EAST));
                })
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, false))
                .thenExecute(() -> helper.setBlock(support, Blocks.REDSTONE_BLOCK))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, true))
                .thenExecute(() -> helper.setBlock(support, Blocks.STONE))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, true))
                .thenExecute(() -> helper.setBlock(support, Blocks.REDSTONE_BLOCK))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(torch, FlipFlopTorchBlock.LIT, false))
                .thenSucceed();
    }
}
