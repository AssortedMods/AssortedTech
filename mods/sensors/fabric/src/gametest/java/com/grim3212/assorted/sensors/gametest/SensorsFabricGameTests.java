package com.grim3212.assorted.sensors.gametest;

import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.lib.test.TestFunctions;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;

import java.util.Collection;

/**
 * Registers this mod's test functions on Fabric. The gametest source set is a dev-only second mod, so
 * nothing in {@code main} references it and the tests never ship. It is that mod's {@code fabric-gametest}
 * entrypoint, whose generator Fabric API hands to vanilla's registry.
 */
public class SensorsFabricGameTests {

    @GameTestGenerator
    public Collection<TestFunction> tests() {
        return TestFunctions.of(Constants.MOD_ID, SensorsGameTests::forEach, SensorsFabricGameTests.class::getResourceAsStream);
    }
}
