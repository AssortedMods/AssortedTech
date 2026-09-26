package com.grim3212.assorted.grates.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Grates. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedgrates/test_instance/<name>.json}.
 */
public final class GratesGameTests {

    private GratesGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        ItemGrateTests.register(out);
        CopperItemGrateTests.register(out);
    }
}
