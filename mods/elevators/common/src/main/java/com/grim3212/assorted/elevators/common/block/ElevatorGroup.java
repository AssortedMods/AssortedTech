package com.grim3212.assorted.elevators.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The footprint of an elevator car: a rectangle of elevator blocks side by side, up to
 * {@link #MAX_SIZE} on each side, that travel together.
 */
public record ElevatorGroup(int minX, int minZ, int sizeX, int sizeZ) {

    public static final int MAX_SIZE = 3;

    /**
     * The rectangle of elevator blocks joined to {@code pos} at its height, or null if they are not
     * a full rectangle or are wider than {@link #MAX_SIZE}.
     */
    public static @Nullable ElevatorGroup find(LevelReader level, BlockPos pos) {
        if (!(level.getBlockState(pos).getBlock() instanceof ElevatorBlock)) {
            return null;
        }

        Set<BlockPos> found = new HashSet<>();
        Deque<BlockPos> open = new ArrayDeque<>();
        open.add(pos);
        found.add(pos);
        while (!open.isEmpty()) {
            BlockPos next = open.poll();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos side = next.relative(direction);
                if (!found.contains(side) && level.getBlockState(side).getBlock() instanceof ElevatorBlock) {
                    // Stop as soon as it is too big to be any allowed car, so a floor of them costs nothing.
                    if (found.size() >= MAX_SIZE * MAX_SIZE) {
                        return null;
                    }
                    found.add(side);
                    open.add(side);
                }
            }
        }

        int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos cell : found) {
            minX = Math.min(minX, cell.getX());
            minZ = Math.min(minZ, cell.getZ());
            maxX = Math.max(maxX, cell.getX());
            maxZ = Math.max(maxZ, cell.getZ());
        }

        int sizeX = maxX - minX + 1;
        int sizeZ = maxZ - minZ + 1;
        if (sizeX > MAX_SIZE || sizeZ > MAX_SIZE || found.size() != sizeX * sizeZ) {
            return null;
        }
        return new ElevatorGroup(minX, minZ, sizeX, sizeZ);
    }

    public boolean contains(int x, int z) {
        return x >= this.minX && x < this.minX + this.sizeX && z >= this.minZ && z < this.minZ + this.sizeZ;
    }

    public List<BlockPos> cells(int y) {
        List<BlockPos> cells = new ArrayList<>(this.sizeX * this.sizeZ);
        for (int x = 0; x < this.sizeX; x++) {
            for (int z = 0; z < this.sizeZ; z++) {
                cells.add(new BlockPos(this.minX + x, y, this.minZ + z));
            }
        }
        return cells;
    }

    /** The blocks around the footprint at height {@code y}, where the shaft's walls and landings are. */
    public List<BlockPos> around(int y) {
        List<BlockPos> around = new ArrayList<>();
        for (BlockPos cell : this.cells(y)) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos side = cell.relative(direction);
                if (!this.contains(side.getX(), side.getZ())) {
                    around.add(side);
                }
            }
        }
        return around;
    }

    public double centerX() {
        return this.minX + this.sizeX / 2.0D;
    }

    public double centerZ() {
        return this.minZ + this.sizeZ / 2.0D;
    }
}
