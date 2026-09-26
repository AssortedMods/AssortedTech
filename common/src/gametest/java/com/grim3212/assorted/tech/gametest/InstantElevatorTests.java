package com.grim3212.assorted.tech.gametest;

import com.grim3212.assorted.tech.common.block.InstantElevatorBlock;
import com.grim3212.assorted.tech.common.block.TechBlocks;
import com.grim3212.assorted.tech.common.handlers.ElevatorInputHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.countInInventory;
import static com.grim3212.assorted.lib.test.TestSupport.rightClick;
import static com.grim3212.assorted.lib.test.TestSupport.stand;
import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;

/**
 * The instant elevators: which one a jump or a sneak sends a player to, and colours.
 */
final class InstantElevatorTests {

    private InstantElevatorTests() {
    }

    private static final BlockPos LOW = new BlockPos(4, 1, 4);
    private static final BlockPos MIDDLE = new BlockPos(4, 4, 4);
    private static final BlockPos HIGH = new BlockPos(4, 7, 4);

    private static final Input SNEAK = new Input(false, false, false, false, false, true, false);

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("instant_elevator_finds_the_next_one", InstantElevatorTests::instantElevatorFindsTheNextOne);
        out.accept("instant_elevator_skips_one_with_no_room", InstantElevatorTests::instantElevatorSkipsOneWithNoRoom);
        out.accept("instant_elevator_jump_goes_up", InstantElevatorTests::instantElevatorJumpGoesUp);
        out.accept("instant_elevator_sneak_goes_down", InstantElevatorTests::instantElevatorSneakGoesDown);
        out.accept("instant_elevator_jump_goes_one_floor", InstantElevatorTests::instantElevatorJumpGoesOneFloor);
        out.accept("instant_elevator_links_its_own_colour_only", InstantElevatorTests::instantElevatorLinksItsOwnColourOnly);
        out.accept("instant_elevator_takes_a_dye", InstantElevatorTests::instantElevatorTakesADye);
    }

    private static void column(GameTestHelper helper) {
        helper.setBlock(LOW, TechBlocks.INSTANT_ELEVATOR.get());
        helper.setBlock(MIDDLE, TechBlocks.INSTANT_ELEVATOR.get());
        helper.setBlock(HIGH, TechBlocks.INSTANT_ELEVATOR.get());
    }

    /** Relative Y of the elevator found from {@code from}, or -1 for none. */
    private static int find(GameTestHelper helper, ServerPlayer player, BlockPos from, boolean up, int range) {
        stand(helper, player, from);
        BlockPos found = ElevatorInputHandler.findDestination(helper.getLevel(), helper.absolutePos(from), up, range, player);
        return found == null ? -1 : found.getY() - helper.absolutePos(BlockPos.ZERO).getY();
    }

    /** Sneaks on a fresh tick after a tick with nothing held. */
    private static void sneak(ServerPlayer player) {
        player.setLastClientInput(Input.EMPTY);
        ElevatorInputHandler.tick(player);
        player.setLastClientInput(SNEAK);
        ElevatorInputHandler.tick(player);
    }

    /** A jump as the server sees one when a client's move packet leaves the ground. */
    private static void jump(ServerPlayer player) {
        ElevatorInputHandler.tick(player);
        player.jumpFromGround();
        ElevatorInputHandler.tick(player);
    }

    private static double relativeY(GameTestHelper helper, ServerPlayer player) {
        return player.getY() - helper.absolutePos(BlockPos.ZERO).getY();
    }

    private static void instantElevatorFindsTheNextOne(GameTestHelper helper) {
        column(helper);
        ServerPlayer player = survivalPlayer(helper);
        helper.assertValueEqual(find(helper, player, LOW, true, 64), MIDDLE.getY(), "up from the lowest");
        helper.assertValueEqual(find(helper, player, MIDDLE, false, 64), LOW.getY(), "down from the middle");
        helper.assertValueEqual(find(helper, player, HIGH, true, 64), -1, "up from the highest");
        helper.assertValueEqual(find(helper, player, LOW, true, 2), -1, "up from the lowest with a range of two");
        helper.succeed();
    }

    private static void instantElevatorSkipsOneWithNoRoom(GameTestHelper helper) {
        column(helper);
        helper.setBlock(MIDDLE.above(2), Blocks.STONE);
        ServerPlayer player = survivalPlayer(helper);
        helper.assertValueEqual(find(helper, player, LOW, true, 64), HIGH.getY(), "up from the lowest past a covered one");
        helper.succeed();
    }

    private static void instantElevatorJumpGoesUp(GameTestHelper helper) {
        column(helper);
        ServerPlayer player = stand(helper, survivalPlayer(helper), LOW);
        jump(player);
        helper.assertValueEqual(relativeY(helper, player), MIDDLE.getY() + 1.0D, "the player's height after jumping");
        helper.succeed();
    }

    private static void instantElevatorSneakGoesDown(GameTestHelper helper) {
        column(helper);
        ServerPlayer player = stand(helper, survivalPlayer(helper), HIGH);
        // The test player is never ticked, so nothing else would find it standing.
        player.setOnGround(true);
        sneak(player);
        helper.assertValueEqual(relativeY(helper, player), MIDDLE.getY() + 1.0D, "the player's height after sneaking");
        helper.succeed();
    }

    private static void instantElevatorJumpGoesOneFloor(GameTestHelper helper) {
        column(helper);
        ServerPlayer player = stand(helper, survivalPlayer(helper), LOW);
        jump(player);
        ElevatorInputHandler.tick(player);
        helper.assertValueEqual(relativeY(helper, player), MIDDLE.getY() + 1.0D, "the player's height a tick after one jump");
        helper.succeed();
    }

    /** A red one in between is passed over; a camouflaged one of the same colour counts. */
    private static void instantElevatorLinksItsOwnColourOnly(GameTestHelper helper) {
        helper.setBlock(LOW, TechBlocks.INSTANT_ELEVATOR.get());
        helper.setBlock(MIDDLE, TechBlocks.INSTANT_ELEVATOR.get().defaultBlockState().setValue(InstantElevatorBlock.COLOR, DyeColor.RED));
        helper.setBlock(HIGH, TechBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get());
        ServerPlayer player = survivalPlayer(helper);
        helper.assertValueEqual(find(helper, player, LOW, true, 64), HIGH.getY(), "up from the lowest past a red one");
        helper.succeed();
    }

    private static void instantElevatorTakesADye(GameTestHelper helper) {
        helper.setBlock(LOW, TechBlocks.INSTANT_ELEVATOR.get());
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.DYE.pick(DyeColor.LIME), 2));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(LOW));
        helper.assertBlockProperty(LOW, InstantElevatorBlock.COLOR, DyeColor.LIME);
        helper.assertValueEqual(countInInventory(player, Items.DYE.pick(DyeColor.LIME)), 1, "lime dye left");
        helper.succeed();
    }
}
