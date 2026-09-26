package com.grim3212.assorted.elevators.gametest;

import com.grim3212.assorted.elevators.common.block.ElevatorGroup;
import com.grim3212.assorted.elevators.common.block.ElevatorShaft;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.block.blockentity.CamouflageBlockEntity;
import com.grim3212.assorted.elevators.common.entity.ElevatorCarEntity;
import com.grim3212.assorted.elevators.common.entity.ElevatorsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.countInInventory;
import static com.grim3212.assorted.lib.test.TestSupport.hitSide;
import static com.grim3212.assorted.lib.test.TestSupport.rightClick;
import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;

/**
 * The camouflaged blocks: taking a disguise and giving it back, and a car keeping each block's
 * disguise from stop to stop.
 */
final class CamouflageTests {

    private CamouflageTests() {
    }

    private static final BlockPos FLOOR = new BlockPos(4, 1, 4);
    private static final BlockPos BOTTOM = FLOOR.above();
    private static final BlockPos CEILING = new BlockPos(4, 8, 4);
    private static final BlockPos TOP = CEILING.below(ElevatorShaft.HEADROOM + 1);

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("camouflaged_instant_elevator_wears_a_disguise", CamouflageTests::camouflagedInstantElevatorWearsADisguise);
        out.accept("camouflaged_elevator_keeps_its_disguise_on_the_way", CamouflageTests::camouflagedElevatorKeepsItsDisguiseOnTheWay);
        out.accept("elevator_car_saves_its_disguises", CamouflageTests::elevatorCarSavesItsDisguises);
        out.accept("elevator_car_syncs_its_disguises", CamouflageTests::elevatorCarSyncsItsDisguises);
        out.accept("camouflaged_elevator_landing_still_calls_the_car", CamouflageTests::camouflagedElevatorLandingStillCallsTheCar);
        out.accept("elevator_can_be_built_against", CamouflageTests::elevatorCanBeBuiltAgainst);
        out.accept("elevator_car_of_wood_and_stone_moves_as_one", CamouflageTests::elevatorCarOfWoodAndStoneMovesAsOne);
        out.accept("camouflage_glows_as_its_disguise", CamouflageTests::camouflageGlowsAsItsDisguise);
        out.accept("elevator_car_carries_its_light", CamouflageTests::elevatorCarCarriesItsLight);
    }

    private static void disguise(GameTestHelper helper, BlockPos rel, Block as) {
        helper.getBlockEntity(rel, CamouflageBlockEntity.class).setDisguise(as.defaultBlockState());
    }

    private static void camouflagedInstantElevatorWearsADisguise(GameTestHelper helper) {
        BlockPos pos = BOTTOM;
        helper.setBlock(pos, ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get());
        CamouflageBlockEntity camouflage = helper.getBlockEntity(pos, CamouflageBlockEntity.class);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(Items.GLASS));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(pos));
        helper.assertTrue(camouflage.getDisguise().isAir(), "it took glass, which it cannot draw as the real thing");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.OAK_LOG));
        rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(pos));
        helper.assertTrue(camouflage.getDisguise().is(Blocks.OAK_LOG), "it is disguised as " + camouflage.getDisguise());
        helper.assertValueEqual(countInInventory(player, Items.OAK_LOG), 1, "logs left, as a disguise is not used up");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);
        rightClick(player, helper.getLevel(), ItemStack.EMPTY, helper.absolutePos(pos));
        helper.assertTrue(camouflage.getDisguise().isAir(), "sneaking with an empty hand left it as " + camouflage.getDisguise());
        helper.succeed();
    }

    /** A two block car, one plain and one disguised, arrives as the same two blocks. */
    private static void camouflagedElevatorKeepsItsDisguiseOnTheWay(GameTestHelper helper) {
        for (BlockPos column : List.of(BlockPos.ZERO, BlockPos.ZERO.east())) {
            helper.setBlock(FLOOR.offset(column), Blocks.STONE);
            helper.setBlock(CEILING.offset(column), Blocks.STONE);
        }
        helper.setBlock(BOTTOM, ElevatorsBlocks.ELEVATOR.get());
        helper.setBlock(BOTTOM.east(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        disguise(helper, BOTTOM.east(), Blocks.OAK_LOG);

        helper.assertTrue(ElevatorShaft.send(helper.getLevel(), helper.absolutePos(BOTTOM), null), "the car did not set off");
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(ElevatorsBlocks.ELEVATOR.get(), TOP);
            helper.assertBlockPresent(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), TOP.east());
            BlockState disguise = helper.getBlockEntity(TOP.east(), CamouflageBlockEntity.class).getDisguise();
            helper.assertTrue(disguise.is(Blocks.OAK_LOG), "the camouflaged block arrived as " + disguise);
        });
    }

    /** A 2 by 2 car of a disguised block, an undisguised camouflaged block and two plain ones. */
    private static ElevatorCarEntity mixedCar(GameTestHelper helper) {
        List<Optional<BlockState>> cells = new ArrayList<>(Collections.nCopies(ElevatorGroup.MAX_SIZE * ElevatorGroup.MAX_SIZE, Optional.<BlockState>empty()));
        cells.set(ElevatorCarEntity.cellIndex(1, 0), ElevatorCarEntity.camouflagedCell(Blocks.OAK_LOG.defaultBlockState()));
        cells.set(ElevatorCarEntity.cellIndex(0, 1), ElevatorCarEntity.camouflagedCell(Blocks.AIR.defaultBlockState()));
        BlockPos min = helper.absolutePos(BOTTOM);
        return new ElevatorCarEntity(helper.getLevel(), new ElevatorGroup(min.getX(), min.getZ(), 2, 2), min.getY(), min.getY() + 3, 0.2F, cells);
    }

    private static void assertSameCells(GameTestHelper helper, ElevatorCarEntity copy, ElevatorCarEntity car, String how) {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                helper.assertValueEqual(copy.getCellLook(x, z), car.getCellLook(x, z), "the look of cell " + x + ", " + z + " " + how);
                helper.assertValueEqual(copy.getCellBlock(x, z).getBlock(), car.getCellBlock(x, z).getBlock(), "the block of cell " + x + ", " + z + " " + how);
            }
        }
        helper.assertTrue(copy.getCellBlock(0, 1).is(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()), "the undisguised camouflaged cell came " + how + " as " + copy.getCellBlock(0, 1));
        helper.assertTrue(copy.getCellLook(1, 0).is(Blocks.OAK_LOG), "the disguised cell came " + how + " as " + copy.getCellLook(1, 0));
    }

    /** What a chunk save in mid trip does to a car: each block's disguise comes back as it was. */
    private static void elevatorCarSavesItsDisguises(GameTestHelper helper) {
        ElevatorCarEntity car = mixedCar(helper);

        ProblemReporter.Collector problems = new ProblemReporter.Collector();
        TagValueOutput output = TagValueOutput.createWithContext(problems, helper.getLevel().registryAccess());
        car.saveWithoutId(output);
        ElevatorCarEntity copy = new ElevatorCarEntity(ElevatorsEntities.ELEVATOR_CAR.get(), helper.getLevel());
        copy.load(TagValueInput.create(problems, helper.getLevel().registryAccess(), output.buildResult()));
        helper.assertTrue(problems.isEmpty(), "the car reported serialization errors: " + problems.getReport());
        assertSameCells(helper, copy, car, "back from a save");
        helper.succeed();
    }

    /** What a client is told: its data packet, written and read back as the network does, draws the same cells. */
    private static void elevatorCarSyncsItsDisguises(GameTestHelper helper) {
        ElevatorCarEntity car = mixedCar(helper);
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        ClientboundSetEntityDataPacket.STREAM_CODEC.encode(buffer, new ClientboundSetEntityDataPacket(car.getId(), car.getEntityData().getNonDefaultValues()));
        ClientboundSetEntityDataPacket received = ClientboundSetEntityDataPacket.STREAM_CODEC.decode(buffer);

        ElevatorCarEntity copy = new ElevatorCarEntity(ElevatorsEntities.ELEVATOR_CAR.get(), helper.getLevel());
        copy.getEntityData().assignValues(received.packedItems());
        assertSameCells(helper, copy, car, "over the network");
        helper.succeed();
    }

    private static void camouflagedElevatorLandingStillCallsTheCar(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE);
        helper.setBlock(CEILING, Blocks.STONE);
        helper.setBlock(BOTTOM, ElevatorsBlocks.ELEVATOR.get());
        BlockPos landing = BOTTOM.above(3).east();
        helper.setBlock(landing, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get());
        disguise(helper, landing, Blocks.STONE_BRICKS);
        helper.setBlock(landing.east(), Blocks.REDSTONE_BLOCK);
        helper.succeedWhen(() -> helper.assertBlockPresent(ElevatorsBlocks.ELEVATOR.get(), landing.below().west()));
    }

    /** Placing the next block of a car against one already down builds the car rather than sending that one off. */
    private static void elevatorCanBeBuiltAgainst(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE);
        helper.setBlock(BOTTOM, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        disguise(helper, BOTTOM, Blocks.OAK_PLANKS);
        ServerPlayer player = survivalPlayer(helper, new ItemStack(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()));
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(BOTTOM), Direction.EAST));
        helper.assertBlockPresent(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), BOTTOM);
        helper.assertBlockPresent(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), BOTTOM.east());
        helper.succeed();
    }

    private static void twoWideShaft(GameTestHelper helper) {
        for (BlockPos column : List.of(BlockPos.ZERO, BlockPos.ZERO.east())) {
            helper.setBlock(FLOOR.offset(column), Blocks.STONE);
            helper.setBlock(CEILING.offset(column), Blocks.STONE);
        }
    }

    private static void elevatorCarOfWoodAndStoneMovesAsOne(GameTestHelper helper) {
        twoWideShaft(helper);
        helper.setBlock(BOTTOM, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        helper.setBlock(BOTTOM.east(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        disguise(helper, BOTTOM, Blocks.OAK_PLANKS);
        disguise(helper, BOTTOM.east(), Blocks.STONE);

        helper.assertTrue(ElevatorShaft.send(helper.getLevel(), helper.absolutePos(BOTTOM), null), "the car did not set off");
        helper.assertBlockNotPresent(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), BOTTOM.east());
        helper.succeedWhen(() -> {
            helper.assertTrue(helper.getBlockEntity(TOP, CamouflageBlockEntity.class).getDisguise().is(Blocks.OAK_PLANKS), "the wooden block did not arrive as wood");
            helper.assertTrue(helper.getBlockEntity(TOP.east(), CamouflageBlockEntity.class).getDisguise().is(Blocks.STONE), "the stone block did not arrive as stone");
        });
    }

    private static int blockLight(GameTestHelper helper, BlockPos rel) {
        return helper.getLevel().getBrightness(LightLayer.BLOCK, helper.absolutePos(rel));
    }

    private static void camouflageGlowsAsItsDisguise(GameTestHelper helper) {
        helper.setBlock(BOTTOM, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get());
        disguise(helper, BOTTOM, Blocks.GLOWSTONE);
        helper.succeedWhen(() -> helper.assertValueEqual(blockLight(helper, BOTTOM.above()), Blocks.GLOWSTONE.defaultBlockState().getLightEmission() - 1, "block light beside a glowstone disguise"));
    }

    private static boolean lightInShaft(GameTestHelper helper) {
        for (int y = BOTTOM.getY(); y < CEILING.getY(); y++) {
            if (helper.getBlockState(BOTTOM.atY(y)).is(Blocks.LIGHT)) {
                return true;
            }
        }
        return false;
    }

    /** A glowing car lights its way up with light blocks, and leaves none behind when it lands. */
    private static void elevatorCarCarriesItsLight(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE);
        helper.setBlock(CEILING, Blocks.STONE);
        helper.setBlock(BOTTOM, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        disguise(helper, BOTTOM, Blocks.GLOWSTONE);
        helper.assertTrue(ElevatorShaft.send(helper.getLevel(), helper.absolutePos(BOTTOM), null), "the car did not set off");

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(lightInShaft(helper), "no light block in the shaft while the car is moving"))
                .thenWaitUntil(() -> {
                    helper.assertBlockPresent(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), TOP);
                    helper.assertFalse(lightInShaft(helper), "a light block was left in the shaft");
                    helper.assertValueEqual(blockLight(helper, TOP.above()), Blocks.GLOWSTONE.defaultBlockState().getLightEmission() - 1, "block light above the landed car");
                })
                .thenSucceed();
    }
}
