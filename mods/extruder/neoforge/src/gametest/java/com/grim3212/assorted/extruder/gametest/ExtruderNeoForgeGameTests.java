package com.grim3212.assorted.extruder.gametest;

import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.lib.test.TestFunctions;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.Collection;

/**
 * Registers this mod's test functions on NeoForge. It lives in the gametest source set, so nothing in
 * {@code main} references it and the tests never ship. NeoForge finds the {@link GameTestHolder} by
 * its annotation and asks the generator for the tests, built from the shared list and test instance jsons.
 */
@GameTestHolder(Constants.MOD_ID)
public final class ExtruderNeoForgeGameTests {

    private ExtruderNeoForgeGameTests() {
    }

    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        return TestFunctions.of(Constants.MOD_ID, ExtruderGameTests::forEach, ExtruderNeoForgeGameTests.class::getResourceAsStream);
    }
}
