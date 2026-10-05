package com.grim3212.assorted.elevators.common.block;

import com.grim3212.assorted.elevators.ElevatorsCommonMod;
import com.grim3212.assorted.elevators.common.entity.ElevatorCarEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Where an elevator car can go and where it stops: the ends of its shaft, and every
 * {@link ElevatorLandingBlock} beside the shaft on the way.
 */
public final class ElevatorShaft {

    /** Room a rider needs above the car, so going up stops this far under the ceiling. */
    public static final int HEADROOM = 2;

    private ElevatorShaft() {
    }

    /** What a car can pass through and come to rest in: air, fluids, plants and the like. */
    public static boolean isShaft(LevelReader level, BlockPos pos) {
        return level.isInsideBuildHeight(pos) && level.getBlockState(pos).canBeReplaced();
    }

    public static boolean isShaft(LevelReader level, ElevatorGroup group, int y) {
        for (BlockPos cell : group.cells(y)) {
            if (!isShaft(level, cell)) {
                return false;
            }
        }
        return true;
    }

    /** The furthest Y the car at {@code y} can travel to one way, going up only with room for a rider; {@code y} if none. */
    public static int limit(LevelReader level, ElevatorGroup group, int y, boolean up, int maxTravel) {
        int reach = y;
        if (!up) {
            while (y - (reach - 1) <= maxTravel && isShaft(level, group, reach - 1)) {
                reach--;
            }
            return reach;
        }

        for (int i = 1; i <= HEADROOM; i++) {
            if (!isShaft(level, group, y + i)) {
                return y;
            }
        }
        // Each step up only has to check the one new layer at the top of the rider's space.
        while (reach + 1 - y <= maxTravel && isShaft(level, group, reach + 1 + HEADROOM)) {
            reach++;
        }
        return reach;
    }

    /** The first landing one way, or the end of the shaft if there is none before it; {@code y} if it cannot move. */
    public static int nextStop(LevelReader level, ElevatorGroup group, int y, boolean up, int maxTravel) {
        int limit = limit(level, group, y, up, maxTravel);
        int step = up ? 1 : -1;
        for (int stop = y + step; stop != limit + step; stop += step) {
            if (hasLanding(level, group, stop)) {
                return stop;
            }
        }
        return limit;
    }

    /** Whether a landing beside the shaft marks a car at {@code carY} as level with its floor. */
    public static boolean hasLanding(LevelReader level, ElevatorGroup group, int carY) {
        for (BlockPos side : group.around(carY + 1)) {
            if (level.getBlockState(side).getBlock() instanceof ElevatorLandingBlock) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the car at {@code pos} to its next stop: up or down if {@code up} is given, otherwise the
     * way it last went, turning back at the end of the shaft.
     */
    public static boolean send(ServerLevel level, BlockPos pos, @Nullable Boolean up) {
        ElevatorGroup group = ElevatorGroup.find(level, pos);
        if (group == null) {
            fail(level, pos);
            return false;
        }

        int maxTravel = ElevatorsCommonMod.COMMON_CONFIG.elevatorMaxTravel.get();
        boolean first = up != null ? up : level.getBlockState(pos).getValue(ElevatorBlock.GOING_UP);
        int stop = nextStop(level, group, pos.getY(), first, maxTravel);
        if (stop == pos.getY() && up == null) {
            stop = nextStop(level, group, pos.getY(), !first, maxTravel);
        }

        if (stop == pos.getY()) {
            fail(level, pos);
            return false;
        }
        depart(level, group, pos.getY(), stop);
        return true;
    }

    /** Brings the car in the shaft beside {@code landing} level with its floor. */
    public static boolean call(ServerLevel level, BlockPos landing) {
        int target = landing.getY() - 1;
        int maxTravel = ElevatorsCommonMod.COMMON_CONFIG.elevatorMaxTravel.get();

        BlockPos nearest = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos column = landing.relative(direction).atY(target);
            if (!level.getEntitiesOfClass(ElevatorCarEntity.class, new AABB(column).expandTowards(0.0D, maxTravel, 0.0D).expandTowards(0.0D, -maxTravel, 0.0D)).isEmpty()) {
                // Already on its way somewhere; the call waits for the rider to arrive.
                fail(level, landing);
                return false;
            }

            BlockPos found = findCar(level, column, maxTravel);
            if (found != null && (nearest == null || Math.abs(found.getY() - target) < Math.abs(nearest.getY() - target))) {
                nearest = found;
            }
        }

        ElevatorGroup group = nearest == null ? null : ElevatorGroup.find(level, nearest);
        if (group == null) {
            fail(level, landing);
            return false;
        }

        if (nearest.getY() == target) {
            level.playSound(null, landing, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.5F, 1.5F);
            return true;
        }

        boolean up = target > nearest.getY();
        int limit = limit(level, group, nearest.getY(), up, maxTravel);
        if (up ? target > limit : target < limit) {
            fail(level, landing);
            return false;
        }
        depart(level, group, nearest.getY(), target);
        return true;
    }

    /** The nearest elevator block up or down the column from {@code from}, within {@code range}. */
    private static @Nullable BlockPos findCar(LevelReader level, BlockPos from, int range) {
        for (int i = 0; i <= range; i++) {
            for (int dy : i == 0 ? new int[]{0} : new int[]{-i, i}) {
                BlockPos pos = from.above(dy);
                if (level.getBlockState(pos).getBlock() instanceof ElevatorBlock) {
                    return pos;
                }
            }
        }
        return null;
    }

    private static void depart(ServerLevel level, ElevatorGroup group, int y, int target) {
        List<Optional<BlockState>> cells = new ArrayList<>(Collections.nCopies(ElevatorGroup.MAX_SIZE * ElevatorGroup.MAX_SIZE, Optional.<BlockState>empty()));
        for (BlockPos cell : group.cells(y)) {
            if (level.getBlockState(cell).getBlock() instanceof CamouflagedElevatorBlock) {
                cells.set(ElevatorCarEntity.cellIndex(cell.getX() - group.minX(), cell.getZ() - group.minZ()), ElevatorCarEntity.camouflagedCell(CamouflageBlock.getDisguise(level, cell)));
            }
        }
        ElevatorCarEntity car = new ElevatorCarEntity(level, group, y, target, ElevatorsCommonMod.COMMON_CONFIG.elevatorSpeed.get().floatValue(), cells);
        for (BlockPos cell : group.cells(y)) {
            level.setBlock(cell, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
        level.addFreshEntity(car);
        updateLandings(level, group, y);
        level.playSound(null, BlockPos.containing(group.centerX(), y, group.centerZ()), SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, target > y ? 1.2F : 0.8F);
    }

    /** Lets comparators behind the landings at a floor see the car arrive or leave. */
    public static void updateLandings(Level level, ElevatorGroup group, int carY) {
        for (BlockPos side : group.around(carY + 1)) {
            Block block = level.getBlockState(side).getBlock();
            if (block instanceof ElevatorLandingBlock) {
                level.updateNeighbourForOutputSignal(side, block);
            }
        }
    }

    private static void fail(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.5F, 1.2F);
    }
}
