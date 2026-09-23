package com.grim3212.assorted.tech.gametest;

import com.grim3212.assorted.tech.common.block.TechBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The metal mesh: a floor for anything that walks, open air for anything that was dropped.
 */
final class MetalMeshTests {

    private MetalMeshTests() {
    }

    /** The mesh, with a stone floor two blocks under it for whatever comes through. */
    private static final BlockPos MESH = new BlockPos(4, 3, 4);
    private static final BlockPos FLOOR = new BlockPos(4, 1, 4);

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("metal_mesh_drops_items_through", MetalMeshTests::metalMeshDropsItemsThrough);
        out.accept("metal_mesh_carries_a_mob", MetalMeshTests::metalMeshCarriesAMob);
    }

    private static void build(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE);
        helper.setBlock(MESH, TechBlocks.METAL_MESH.get());
    }

    /** An item dropped onto the mesh falls through it and lands on the floor below. */
    private static void metalMeshDropsItemsThrough(GameTestHelper helper) {
        build(helper);
        ItemEntity item = helper.spawnItem(Items.STICK, Vec3.atCenterOf(MESH.above(2)));

        helper.succeedWhen(() -> {
            helper.assertTrue(item.isAlive(), "the dropped stick is gone");
            helper.assertTrue(item.onGround(), "the stick is still falling");
            double relativeY = item.position().y - helper.absolutePos(BlockPos.ZERO).getY();
            helper.assertTrue(relativeY < MESH.getY(), "the stick came to rest at " + relativeY + ", on top of the mesh rather than through it");
        });
    }

    /** A mob on the mesh stands on it, the same as it would on any other block. */
    private static void metalMeshCarriesAMob(GameTestHelper helper) {
        build(helper);
        Pig pig = helper.spawnWithNoFreeWill(EntityTypes.PIG, MESH.above());

        helper.succeedWhen(() -> {
            helper.assertTrue(pig.onGround(), "the pig is still falling");
            double relativeY = pig.position().y - helper.absolutePos(BlockPos.ZERO).getY();
            helper.assertTrue(relativeY >= MESH.getY() + 1, "the pig fell through the mesh to " + relativeY);
        });
    }
}
