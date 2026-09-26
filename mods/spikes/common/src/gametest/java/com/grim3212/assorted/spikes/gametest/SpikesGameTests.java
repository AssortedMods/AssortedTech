package com.grim3212.assorted.spikes.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for Assorted Spikes. The tests live in small {@code <Feature>Tests} classes;
 * this only lists them. Each name needs a matching {@code data/assortedspikes/test_instance/<name>.json}.
 */
public final class SpikesGameTests {

    private SpikesGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        AssetTests.register(out);
        AliasTests.register(out);
        CrossLoaderDataTests.register(out);
        SpikeTests.register(out);
        TooltipTests.register(out);
    }
}
