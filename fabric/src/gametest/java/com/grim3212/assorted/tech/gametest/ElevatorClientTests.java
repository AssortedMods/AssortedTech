package com.grim3212.assorted.tech.gametest;

import com.grim3212.assorted.tech.common.block.ElevatorShaft;
import com.grim3212.assorted.tech.common.block.TechBlocks;
import com.grim3212.assorted.tech.common.block.blockentity.CamouflageBlockEntity;
import com.grim3212.assorted.tech.common.entity.ElevatorCarEntity;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * The elevators under a real client: riding a car depends on the client moving its own player,
 * the jump and sneak controls on the input it sends, and a disguise on the model loader.
 */
final class ElevatorClientTests {

    private ElevatorClientTests() {
    }

    /** How far the rider's feet may drift from the top of the car without it counting as falling off. */
    private static final double RIDE_TOLERANCE = 0.1D;

    static void run(ClientGameTestContext context, TestSingleplayerContext world) {
        world.getServer().runCommand("gamemode survival @a");
        world.getConnection().waitForChunksRender();
        BlockPos start = context.computeOnClient(client -> client.player.blockPosition());

        rideTheElevator(context, world, start.east(3));
        rideABigCar(context, world, start.south(5));
        useTheInstantElevator(context, world, start.west(3));
        disguise(context, world, start.north(3));
    }

    private static void rideTheElevator(ClientGameTestContext context, TestSingleplayerContext world, BlockPos bottom) {
        BlockPos ceiling = bottom.above(8);
        BlockPos top = ceiling.below(ElevatorShaft.HEADROOM + 1);
        world.getServer().runCommand("setblock " + pos(ceiling) + " stone");
        world.getServer().runCommand("setblock " + pos(bottom) + " assortedtech:elevator");
        standOn(context, world, bottom);

        ride(context, bottom, top, () -> use(context, bottom));
        ride(context, top, bottom, () -> use(context, top));
    }

    /** A 3 by 3 car with one disguised block, ridden from a corner, sent up by a jump and down by a sneak. */
    private static void rideABigCar(ClientGameTestContext context, TestSingleplayerContext world, BlockPos center) {
        BlockPos top = center.above(5 - ElevatorShaft.HEADROOM - 1);
        world.getServer().runCommand("fill " + pos(center.offset(-1, 5, -1)) + " " + pos(center.offset(1, 5, 1)) + " stone");
        world.getServer().runCommand("fill " + pos(center.offset(-1, 0, -1)) + " " + pos(center.offset(1, 0, 1)) + " assortedtech:elevator");
        BlockPos corner = center.offset(1, 0, 1);
        BlockPos disguised = center.offset(-1, 0, -1);
        world.getServer().runCommand("setblock " + pos(disguised) + " assortedtech:camouflaged_elevator");
        world.getServer().runOnServer(server -> {
            if (server.overworld().getBlockEntity(disguised) instanceof CamouflageBlockEntity camouflage) {
                camouflage.setDisguise(Blocks.OAK_PLANKS.defaultBlockState());
            }
        });
        standOn(context, world, corner);

        ride(context, corner, corner.atY(top.getY()), () -> context.getInput().holdKeyFor(options -> options.keyJump, 2));
        ride(context, corner.atY(top.getY()), corner, () -> {
            context.getInput().holdKey(options -> options.keyShift);
            context.waitTicks(3);
            context.getInput().releaseKey(options -> options.keyShift);
        });

        context.waitFor(client -> client.level.getBlockEntity(disguised) instanceof CamouflageBlockEntity camouflage && camouflage.getDisguise().is(Blocks.OAK_PLANKS), 40);
    }

    private static void use(ClientGameTestContext context, BlockPos elevator) {
        context.getInput().lookAt(elevator);
        context.getInput().pressKey(options -> options.keyUse);
    }

    /** Sends the car underfoot with {@code trigger} and checks the player stays on it all the way, once they have landed on it. */
    private static void ride(ClientGameTestContext context, BlockPos from, BlockPos to, Runnable trigger) {
        trigger.run();

        context.waitFor(client -> car(client) != null, 40);
        int ticks = 0;
        boolean aboard = false;
        while (context.computeOnClient(client -> car(client) != null)) {
            double gap = context.computeOnClient(client -> {
                ElevatorCarEntity car = car(client);
                return car == null ? 0.0D : client.player.getY() - car.getBoundingBox().maxY;
            });
            aboard |= Math.abs(gap) <= RIDE_TOLERANCE;
            if (aboard && Math.abs(gap) > RIDE_TOLERANCE) {
                throw new AssertionError("riding from " + from + " to " + to + ", the player's feet were " + gap + " from the top of the car after " + ticks + " ticks");
            }
            if (++ticks > 400) {
                throw new AssertionError("the elevator car from " + from + " never arrived at " + to);
            }
            context.waitTick();
        }

        context.waitFor(client -> client.level.getBlockState(to).is(TechBlocks.ELEVATOR.get()), 40);
        double feet = context.computeOnClient(client -> client.player.getY());
        if (Math.abs(feet - (to.getY() + 1)) > 0.01D) {
            throw new AssertionError("the elevator arrived at " + to + " but left the player at " + feet);
        }
    }

    private static void useTheInstantElevator(ClientGameTestContext context, TestSingleplayerContext world, BlockPos low) {
        BlockPos high = low.above(5);
        world.getServer().runCommand("setblock " + pos(low) + " assortedtech:instant_elevator");
        world.getServer().runCommand("setblock " + pos(high) + " assortedtech:instant_elevator");
        standOn(context, world, low);

        // A jump reads whether the key is down, which a single-tick press never is by the time it looks.
        context.getInput().holdKeyFor(options -> options.keyJump, 2);
        waitToStandOn(context, high, "jumping on the lower instant elevator");

        context.getInput().holdKey(options -> options.keyShift);
        waitToStandOn(context, low, "sneaking on the upper instant elevator");
        context.getInput().releaseKey(options -> options.keyShift);
    }

    /** A disguise set on the server reaches the client, and the camouflage model loads rather than going missing. */
    private static void disguise(ClientGameTestContext context, TestSingleplayerContext world, BlockPos pos) {
        world.getServer().runCommand("setblock " + pos(pos) + " assortedtech:camouflaged_instant_elevator");
        world.getServer().runOnServer(server -> {
            if (server.overworld().getBlockEntity(pos) instanceof CamouflageBlockEntity camouflage) {
                camouflage.setDisguise(Blocks.OAK_LOG.defaultBlockState());
            }
        });

        context.waitFor(client -> client.level.getBlockEntity(pos) instanceof CamouflageBlockEntity camouflage && camouflage.getDisguise().is(Blocks.OAK_LOG), 40);
        boolean missing = context.computeOnClient(client -> {
            var models = client.getModelManager().getBlockStateModelSet();
            return models.get(client.level.getBlockState(pos)) == models.missingModel();
        });
        if (missing) {
            throw new AssertionError("the camouflaged instant elevator has no model");
        }
    }

    private static void standOn(ClientGameTestContext context, TestSingleplayerContext world, BlockPos pos) {
        world.getServer().runCommand("tp @a " + (pos.getX() + 0.5D) + " " + (pos.getY() + 1) + " " + (pos.getZ() + 0.5D));
        waitToStandOn(context, pos, "teleporting onto " + pos);
    }

    private static void waitToStandOn(ClientGameTestContext context, BlockPos pos, String what) {
        try {
            // The whole position, as a stale one from the last step can share the height.
            context.waitFor(client -> client.player.onGround() && BlockPos.containing(client.player.getX(), client.player.getY() - 0.5D, client.player.getZ()).equals(pos) && Math.abs(client.player.getY() - (pos.getY() + 1)) < 0.01D, 60);
        } catch (AssertionError e) {
            double feet = context.computeOnClient(client -> client.player.getY());
            throw new AssertionError(what + " left the player at " + feet + ", not standing on " + pos, e);
        }
    }

    private static ElevatorCarEntity car(Minecraft client) {
        List<ElevatorCarEntity> cars = client.level.getEntitiesOfClass(ElevatorCarEntity.class, new AABB(client.player.blockPosition()).inflate(2.0D, 12.0D, 2.0D));
        return cars.isEmpty() ? null : cars.getFirst();
    }

    private static String pos(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }
}
