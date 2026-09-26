package com.grim3212.assorted.extruder.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Extruder. The tests live in the {@code *Tests} classes, with
 * helpers in {@code ExtruderTestSupport}; this only lists them.
 */
public final class ExtruderGameTests {

    private ExtruderGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        ExtruderTests.register(out);
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
    }
}
