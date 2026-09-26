package com.grim3212.assorted.tech.gametest;

import com.grim3212.assorted.tech.common.block.ElevatorBlock;
import com.grim3212.assorted.tech.common.block.ElevatorGroup;
import com.grim3212.assorted.tech.common.block.ElevatorShaft;
import com.grim3212.assorted.tech.common.block.TechBlocks;
import com.grim3212.assorted.tech.common.entity.ElevatorCarEntity;
import com.grim3212.assorted.tech.common.handlers.ElevatorInputHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.stand;
import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;

/**
 * The elevator: where its shaft and landings send it, and that it gets there carrying what stands on it.
 */
final class ElevatorTests {

    private ElevatorTests() {
    }

    /** A shaft from a stone floor to a stone ceiling; a car starting at the bottom stops at TOP, two under the ceiling. */
    private static final BlockPos FLOOR = new BlockPos(4, 1, 4);
    private static final BlockPos BOTTOM = FLOOR.above();
    private static final BlockPos CEILING = new BlockPos(4, 8, 4);
    private static final BlockPos TOP = CEILING.below(ElevatorShaft.HEADROOM + 1);
    /** In the shaft's east wall, one above the floor a car at BOTTOM + 2 is level with. */
    private static final BlockPos LANDING = BOTTOM.above(3).east();

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("elevator_finds_the_ends_of_its_shaft", ElevatorTests::elevatorFindsTheEndsOfItsShaft);
        out.accept("elevator_stops_at_a_landing", ElevatorTests::elevatorStopsAtALanding);
        out.accept("elevator_with_no_floor_or_ceiling_stops_at_its_max_travel", ElevatorTests::elevatorWithNoFloorOrCeilingStopsAtItsMaxTravel);
        out.accept("elevator_carries_a_mob_up", ElevatorTests::elevatorCarriesAMobUp);
        out.accept("elevator_turns_back_at_the_top", ElevatorTests::elevatorTurnsBackAtTheTop);
        out.accept("elevator_answers_a_redstone_pulse", ElevatorTests::elevatorAnswersARedstonePulse);
        out.accept("elevator_with_no_headroom_stays_put", ElevatorTests::elevatorWithNoHeadroomStaysPut);
        out.accept("elevator_stops_short_of_a_blocked_shaft", ElevatorTests::elevatorStopsShortOfABlockedShaft);
        out.accept("elevator_landing_calls_the_car", ElevatorTests::elevatorLandingCallsTheCar);
        out.accept("elevator_landing_tells_a_comparator", ElevatorTests::elevatorLandingTellsAComparator);
        out.accept("elevator_crushes_what_is_under_it", ElevatorTests::elevatorCrushesWhatIsUnderIt);
        out.accept("elevator_eases_off_the_mark", ElevatorTests::elevatorEasesOffTheMark);
        out.accept("elevator_three_by_three_moves_as_one", ElevatorTests::elevatorThreeByThreeMovesAsOne);
        out.accept("elevator_wider_than_three_stays_put", ElevatorTests::elevatorWiderThanThreeStaysPut);
        out.accept("elevator_goes_down_when_sneaked_on", ElevatorTests::elevatorGoesDownWhenSneakedOn);
    }

    private static void shaft(GameTestHelper helper, BlockPos elevator) {
        helper.setBlock(FLOOR, Blocks.STONE);
        helper.setBlock(CEILING, Blocks.STONE);
        helper.setBlock(elevator, TechBlocks.ELEVATOR.get());
    }

    private static boolean send(GameTestHelper helper, BlockPos rel) {
        return ElevatorShaft.send(helper.getLevel(), helper.absolutePos(rel), null);
    }

    private static ElevatorGroup group(GameTestHelper helper, BlockPos rel) {
        ElevatorGroup group = ElevatorGroup.find(helper.getLevel(), helper.absolutePos(rel));
        helper.assertTrue(group != null, "no elevator car at " + rel);
        return group;
    }

    private static int relativeY(GameTestHelper helper, int absoluteY) {
        return absoluteY - helper.absolutePos(BlockPos.ZERO).getY();
    }

    private static void elevatorFindsTheEndsOfItsShaft(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        ElevatorGroup group = group(helper, BOTTOM);
        int bottom = helper.absolutePos(BOTTOM).getY();
        helper.assertValueEqual(relativeY(helper, ElevatorShaft.limit(helper.getLevel(), group, bottom, true, 64)), TOP.getY(), "how far up the bottom car can go");
        helper.assertValueEqual(relativeY(helper, ElevatorShaft.limit(helper.getLevel(), group, bottom, true, 2)), BOTTOM.getY() + 2, "how far up it can go when it may only travel two");
        helper.assertValueEqual(relativeY(helper, ElevatorShaft.limit(helper.getLevel(), group, bottom, false, 64)), BOTTOM.getY(), "how far down it can go from the floor");
        helper.succeed();
    }

    private static void elevatorStopsAtALanding(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.setBlock(LANDING, TechBlocks.ELEVATOR_LANDING.get());
        int stop = ElevatorShaft.nextStop(helper.getLevel(), group(helper, BOTTOM), helper.absolutePos(BOTTOM).getY(), true, 64);
        helper.assertValueEqual(relativeY(helper, stop), LANDING.getY() - 1, "the first stop up");
        helper.succeed();
    }

    /** Hanging in open air, with nothing above or below to stop it, it goes as far as it may and no further. */
    private static void elevatorWithNoFloorOrCeilingStopsAtItsMaxTravel(GameTestHelper helper) {
        BlockPos car = new BlockPos(4, 4, 4);
        helper.setBlock(car, TechBlocks.ELEVATOR.get());
        ElevatorGroup group = group(helper, car);
        int y = helper.absolutePos(car).getY();
        helper.assertValueEqual(relativeY(helper, ElevatorShaft.nextStop(helper.getLevel(), group, y, true, 2)), car.getY() + 2, "the stop up");
        helper.assertValueEqual(relativeY(helper, ElevatorShaft.nextStop(helper.getLevel(), group, y, false, 3)), car.getY() - 3, "the stop down");
        helper.succeed();
    }

    private static void elevatorCarriesAMobUp(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, BOTTOM.above());
        helper.runAfterDelay(5, () -> helper.assertTrue(send(helper, BOTTOM), "the elevator did not set off"));

        helper.succeedWhen(() -> {
            helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), TOP);
            double relativeY = pig.getY() - helper.absolutePos(BlockPos.ZERO).getY();
            helper.assertTrue(Math.abs(relativeY - (TOP.getY() + 1)) < 0.01D, "the pig was left at " + relativeY + " rather than riding to the top");
        });
    }

    /** It keeps going the way it last went, so a car that came up and cannot go further comes back down. */
    private static void elevatorTurnsBackAtTheTop(GameTestHelper helper) {
        shaft(helper, TOP);
        helper.setBlock(TOP, TechBlocks.ELEVATOR.get().defaultBlockState().setValue(ElevatorBlock.GOING_UP, true));
        helper.assertTrue(send(helper, TOP), "the elevator did not set off");
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), BOTTOM);
            helper.assertBlockProperty(BOTTOM, ElevatorBlock.GOING_UP, false);
        });
    }

    private static void elevatorAnswersARedstonePulse(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.setBlock(BOTTOM.west(), Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(() -> helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), TOP));
    }

    private static void elevatorWithNoHeadroomStaysPut(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.setBlock(BOTTOM.above(ElevatorShaft.HEADROOM), Blocks.STONE);
        helper.assertFalse(send(helper, BOTTOM), "the elevator set off with no room for a rider above it");
        helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), BOTTOM);
        helper.succeed();
    }

    /** A block built into the shaft mid-trip stops the car where its rider still has room. */
    private static void elevatorStopsShortOfABlockedShaft(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.assertTrue(send(helper, BOTTOM), "the elevator did not set off");
        helper.setBlock(TOP.above(ElevatorShaft.HEADROOM), Blocks.STONE);
        helper.succeedWhen(() -> helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), TOP.below()));
    }

    private static void elevatorLandingCallsTheCar(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.setBlock(LANDING, TechBlocks.ELEVATOR_LANDING.get());
        // Powers the landing and nothing else.
        helper.setBlock(LANDING.east(), Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(() -> helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), LANDING.below().west()));
    }

    private static void elevatorLandingTellsAComparator(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        BlockPos here = BOTTOM.above().east();
        helper.setBlock(here, TechBlocks.ELEVATOR_LANDING.get());
        helper.setBlock(LANDING, TechBlocks.ELEVATOR_LANDING.get());
        helper.assertValueEqual(signal(helper, here), 15, "the landing the car is at");
        helper.assertValueEqual(signal(helper, LANDING), 0, "a landing the car is not at");
        helper.succeed();
    }

    private static int signal(GameTestHelper helper, BlockPos rel) {
        BlockState state = helper.getBlockState(rel);
        return state.getAnalogOutputSignal(helper.getLevel(), helper.absolutePos(rel), Direction.EAST);
    }

    private static void elevatorCrushesWhatIsUnderIt(GameTestHelper helper) {
        shaft(helper, TOP);
        helper.setBlock(TOP, TechBlocks.ELEVATOR.get().defaultBlockState().setValue(ElevatorBlock.GOING_UP, false));
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, BOTTOM);
        helper.assertTrue(send(helper, TOP), "the elevator did not set off");
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), BOTTOM);
            helper.assertTrue(pig.isDeadOrDying() || pig.getHealth() < pig.getMaxHealth(), "the pig under the car was not hurt");
        });
    }

    /** It sets off slowly rather than at full speed. */
    private static void elevatorEasesOffTheMark(GameTestHelper helper) {
        shaft(helper, BOTTOM);
        helper.assertTrue(send(helper, BOTTOM), "the elevator did not set off");
        helper.runAfterDelay(1, () -> {
            List<ElevatorCarEntity> cars = helper.getLevel().getEntitiesOfClass(ElevatorCarEntity.class, new AABB(helper.absolutePos(BOTTOM)).inflate(1.0D, 3.0D, 1.0D));
            helper.assertValueEqual(cars.size(), 1, "cars in the shaft");
            double moved = cars.getFirst().getY() - helper.absolutePos(BOTTOM).getY();
            helper.assertTrue(moved > 0.0D && moved < 0.1D, "the car moved " + moved + " on its first tick, not a gentle start");
            helper.succeed();
        });
    }

    private static void elevatorThreeByThreeMovesAsOne(GameTestHelper helper) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                helper.setBlock(FLOOR.offset(x, 0, z), Blocks.STONE);
                helper.setBlock(CEILING.offset(x, 0, z), Blocks.STONE);
                helper.setBlock(BOTTOM.offset(x, 0, z), TechBlocks.ELEVATOR.get());
            }
        }
        helper.assertTrue(send(helper, BOTTOM.offset(1, 0, 1)), "the car did not set off");
        helper.succeedWhen(() -> {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), TOP.offset(x, 0, z));
                }
            }
        });
    }

    private static void elevatorWiderThanThreeStaysPut(GameTestHelper helper) {
        for (int x = -2; x <= 1; x++) {
            helper.setBlock(BOTTOM.offset(x, 0, 0), TechBlocks.ELEVATOR.get());
        }
        helper.assertFalse(send(helper, BOTTOM), "a row of four set off");
        helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), BOTTOM.west(2));
        helper.succeed();
    }

    private static void elevatorGoesDownWhenSneakedOn(GameTestHelper helper) {
        shaft(helper, TOP);
        ServerPlayer player = stand(helper, survivalPlayer(helper), TOP);
        // The test player is never ticked, so nothing else would find it standing.
        player.setOnGround(true);
        player.setLastClientInput(Input.EMPTY);
        ElevatorInputHandler.tick(player);
        player.setLastClientInput(new Input(false, false, false, false, false, true, false));
        ElevatorInputHandler.tick(player);
        helper.assertBlockNotPresent(TechBlocks.ELEVATOR.get(), TOP);
        helper.succeedWhen(() -> helper.assertBlockPresent(TechBlocks.ELEVATOR.get(), BOTTOM));
    }
}
