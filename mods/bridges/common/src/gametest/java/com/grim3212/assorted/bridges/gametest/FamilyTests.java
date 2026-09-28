package com.grim3212.assorted.bridges.gametest;

import com.grim3212.assorted.lib.conditions.PartToggles;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.bridges.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** This part's place in its family: a switch players can turn off, and icons that are real items. */
final class FamilyTests {

    private FamilyTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("part_can_be_turned_off", FamilyTests::canBeTurnedOff);
        out.accept("family_icons_are_items", FamilyTests::iconsAreItems);
    }

    private static void canBeTurnedOff(GameTestHelper helper) {
        List<String> problems = PartToggles.problems(helper.getLevel().getServer().getResourceManager(), Constants.MOD_ID);
        helper.assertTrue(problems.isEmpty(), String.join("; ", problems));
        helper.succeed();
    }

    private static void iconsAreItems(GameTestHelper helper) {
        helper.assertTrue(Families.members(Constants.FAMILY_ID).contains(Constants.MOD_ID), Constants.MOD_ID + " is not in " + Constants.FAMILY_ID);
        for (Identifier icon : Families.icons(Constants.FAMILY_ID)) {
            helper.assertTrue(BuiltInRegistries.ITEM.containsKey(icon), "family icon " + icon + " is not an item");
        }
        helper.succeed();
    }
}
