package com.grim3212.assorted.elevators.common.handlers;

import com.grim3212.assorted.elevators.ElevatorsCommonMod;
import com.grim3212.assorted.elevators.common.block.ElevatorBlock;
import com.grim3212.assorted.elevators.common.block.ElevatorShaft;
import com.grim3212.assorted.elevators.common.block.InstantElevatorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Jumping or sneaking on an elevator: an instant elevator takes the player to the next one of its
 * colour, an elevator car sets off up or down. Read from what vanilla already tells the server.
 */
public final class ElevatorInputHandler {

    /** The server counts each jump it sees in {@link Stats#JUMP}, which a tap too quick for any one tick's input still reaches. */
    private static final Map<ServerPlayer, Integer> LAST_JUMPS = new WeakHashMap<>();
    /** Last tick's sneak key, so holding it down does not keep going. */
    private static final Map<ServerPlayer, Boolean> LAST_SHIFT = new WeakHashMap<>();

    private ElevatorInputHandler() {
    }

    /** Called by each loader at the end of every server level tick. */
    public static void tick(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            tick(player);
        }
    }

    public static void tick(ServerPlayer player) {
        int jumps = player.getStats().getValue(Stats.CUSTOM, Stats.JUMP);
        boolean shift = player.getLastClientInput().shift();
        Integer lastJumps = LAST_JUMPS.put(player, jumps);
        Boolean lastShift = LAST_SHIFT.put(player, shift);
        if (lastJumps == null || lastShift == null) {
            return;
        }

        boolean up = jumps > lastJumps;
        boolean down = shift && !lastShift && player.onGround();
        if (up == down || player.isPassenger() || player.isSpectator() || player.getAbilities().flying) {
            return;
        }

        BlockPos from = elevatorUnder(player);
        if (from == null) {
            return;
        }

        ServerLevel level = player.level();
        if (level.getBlockState(from).getBlock() instanceof ElevatorBlock) {
            ElevatorShaft.send(level, from, up);
            return;
        }

        BlockPos to = findDestination(level, from, up, ElevatorsCommonMod.COMMON_CONFIG.instantElevatorRange.get(), player);
        if (to != null) {
            teleport(player, from, to);
        }
    }

    /** The elevator of either kind the player is standing on or has just jumped off, or null. */
    private static @Nullable BlockPos elevatorUnder(ServerPlayer player) {
        Level level = player.level();
        // By the time the jump reaches the server the player can already be most of a block up.
        BlockPos standing = BlockPos.containing(player.getX(), player.getY() - 0.5D, player.getZ());
        BlockPos pos = level.getBlockState(standing).getCollisionShape(level, standing).isEmpty() ? standing.below() : standing;
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof InstantElevatorBlock || state.getBlock() instanceof ElevatorBlock ? pos : null;
    }

    /**
     * The nearest instant elevator of the same colour above or below {@code from}, within {@code range},
     * that {@code entity} fits on top of. Ones with no room are skipped rather than stopping the search.
     */
    public static @Nullable BlockPos findDestination(Level level, BlockPos from, boolean up, int range, Entity entity) {
        DyeColor color = level.getBlockState(from).getValue(InstantElevatorBlock.COLOR);
        for (int i = 1; i <= range; i++) {
            BlockPos pos = up ? from.above(i) : from.below(i);
            if (!level.isInsideBuildHeight(pos)) {
                return null;
            }

            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof InstantElevatorBlock && state.getValue(InstantElevatorBlock.COLOR) == color
                    && level.noCollision(entity, entity.getBoundingBox().move(0.0D, pos.getY() + 1 - entity.getY(), 0.0D))) {
                return pos;
            }
        }
        return null;
    }

    private static void teleport(ServerPlayer player, BlockPos from, BlockPos to) {
        Vec3 destination = new Vec3(player.getX(), to.getY() + 1, player.getZ());
        // Absolute zero motion, so the jump that got them here does not carry on past the floor.
        player.connection.teleport(new PositionMoveRotation(destination, Vec3.ZERO, 0.0F, 0.0F), Relative.ROTATION);
        player.resetFallDistance();

        ServerLevel level = player.level();
        level.playSound(null, from, SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS, 0.4F, 1.4F);
        level.playSound(null, to, SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS, 0.4F, 1.4F);
    }
}
