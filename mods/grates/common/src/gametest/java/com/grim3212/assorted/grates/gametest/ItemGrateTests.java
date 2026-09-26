package com.grim3212.assorted.grates.gametest;

import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.block.ItemGrateBlock;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.grates.gametest.GratesTestSupport.name;

/**
 * Every item grate: a floor for anything that walks, open air for anything that was dropped.
 */
final class ItemGrateTests {

    private ItemGrateTests() {
    }

    /** Grates sit this high, with a stone floor two blocks under each for whatever comes through. */
    private static final int GRATE_Y = 3;
    private static final int FLOOR_Y = 1;

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("item_grates_drop_items_through", ItemGrateTests::itemGratesDropItemsThrough);
        out.accept("item_grates_carry_a_mob", ItemGrateTests::itemGratesCarryAMob);
        out.accept("grates_with_no_material_are_uncraftable", ItemGrateTests::gratesWithNoMaterialAreUncraftable);
    }

    /** Two apart in both axes, five to a row, so all twenty fit the nine by nine box. */
    private static BlockPos grateSlot(int index) {
        return new BlockPos((index % 5) * 2, GRATE_Y, (index / 5) * 2);
    }

    private static List<ItemGrateBlock> build(GameTestHelper helper) {
        List<ItemGrateBlock> grates = GratesBlocks.allItemGrates().stream().map(IRegistryObject::get).toList();
        for (int i = 0; i < grates.size(); i++) {
            BlockPos grate = grateSlot(i);
            helper.setBlock(grate.atY(FLOOR_Y), Blocks.STONE);
            helper.setBlock(grate, grates.get(i));
        }
        return grates;
    }

    /** An item dropped onto each grate falls through it and lands on the floor below. */
    private static void itemGratesDropItemsThrough(GameTestHelper helper) {
        List<ItemGrateBlock> grates = build(helper);
        List<ItemEntity> sticks = new ArrayList<>();
        for (int i = 0; i < grates.size(); i++) {
            sticks.add(helper.spawnItem(Items.STICK, Vec3.atCenterOf(grateSlot(i).above(2))));
        }

        helper.succeedWhen(() -> {
            for (int i = 0; i < grates.size(); i++) {
                ItemEntity stick = sticks.get(i);
                helper.assertTrue(stick.isAlive(), "the stick dropped on the " + name(grates.get(i)) + " is gone");
                helper.assertTrue(stick.onGround(), "the stick dropped on the " + name(grates.get(i)) + " is still falling");
                double relativeY = stick.position().y - helper.absolutePos(BlockPos.ZERO).getY();
                helper.assertTrue(relativeY < GRATE_Y, "the stick came to rest at " + relativeY + ", on top of the " + name(grates.get(i)) + " rather than through it");
            }
        });
    }

    /** A mob on each grate stands on it, the same as it would on any other block. */
    private static void itemGratesCarryAMob(GameTestHelper helper) {
        List<ItemGrateBlock> grates = build(helper);
        List<Pig> pigs = new ArrayList<>();
        for (int i = 0; i < grates.size(); i++) {
            pigs.add(helper.spawnWithNoFreeWill(EntityTypes.PIG, grateSlot(i).above()));
        }

        helper.succeedWhen(() -> {
            for (int i = 0; i < grates.size(); i++) {
                Pig pig = pigs.get(i);
                helper.assertTrue(pig.onGround(), "the pig on the " + name(grates.get(i)) + " is still falling");
                double relativeY = pig.position().y - helper.absolutePos(BlockPos.ZERO).getY();
                helper.assertTrue(relativeY >= GRATE_Y + 1, "the pig fell through the " + name(grates.get(i)) + " to " + relativeY);
            }
        });
    }

    /**
     * With {@code hideUncraftableItems} on, a grate is hidden when its material's tag is empty or
     * undefined. The config is off in tests, so this checks the rule, Core's absent tags covering undefined.
     */
    private static void gratesWithNoMaterialAreUncraftable(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        int undefined = 0;
        for (GrateMaterial material : GrateMaterial.values()) {
            Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.get(material.getMaterial());
            boolean noMaterial = tag.isEmpty() || tag.get().size() == 0;
            if (tag.isEmpty()) {
                undefined++;
            }
            if (material.isUncraftable() != noMaterial) {
                wrong.add(material.getSerializedName() + (noMaterial ? " has no material but counts as craftable" : " has a material but counts as uncraftable"));
            }
        }

        helper.assertTrue(wrong.isEmpty(), String.join(", ", wrong));
        helper.assertFalse(GrateMaterial.IRON.isUncraftable(), "an iron grate counts as uncraftable");
        helper.assertFalse(GrateMaterial.GOLD.isUncraftable(), "a gold grate counts as uncraftable");
        helper.assertTrue(undefined > 0, "every grate material tag is defined here, so the undefined case was not exercised");
        helper.succeed();
    }
}
