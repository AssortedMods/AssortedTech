package com.grim3212.assorted.sensors.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Sensors. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedsensors/test_instance/<name>.json}.
 */
public final class SensorsGameTests {

    private SensorsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        SensorTests.register(out);
        GpsSensorTests.register(out);
        TooltipTests.register(out);
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        FamilyTests.register(out);
    }
}
