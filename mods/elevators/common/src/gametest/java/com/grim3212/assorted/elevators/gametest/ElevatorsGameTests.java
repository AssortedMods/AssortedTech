package com.grim3212.assorted.elevators.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Elevators. The tests live in the {@code *Tests} classes, with helpers in
 * {@code ElevatorsTestSupport}; this only lists them. Each name needs a matching {@code data/assortedelevators/test_instance/<name>.json}.
 */
public final class ElevatorsGameTests {

    private ElevatorsGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        ElevatorTests.register(out);
        InstantElevatorTests.register(out);
        CamouflageTests.register(out);
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
    }
}
