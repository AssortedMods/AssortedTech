package com.grim3212.assorted.spikes.gametest;

import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.test.TestSupport;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Item tooltip lines, which come from data components. NeoForge also builds the full tooltip on the
 * server, so there it is checked too.
 */
final class TooltipTests {

    private TooltipTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("spike_tooltips_name_their_damage", TooltipTests::spikeTooltipsNameTheirDamage);
    }

    /** Every spike names its damage in an {@code assortedlib:description}. */
    private static void spikeTooltipsNameTheirDamage(GameTestHelper helper) {
        for (IRegistryObject<? extends Block> spike : SpikesBlocks.SPIKES) {
            assertDescribed(helper, new ItemStack(spike.get()), "tooltip.spike.damage");
        }
        helper.succeed();
    }

    /** The stack's description is one line whose key starts with {@code keyPrefix}. */
    private static void assertDescribed(GameTestHelper helper, ItemStack stack, String keyPrefix) {
        List<String> keys = TestSupport.tooltipKeys(helper, stack, LibDataComponents.DESCRIPTION.get());
        helper.assertTrue(keys.size() == 1 && keys.get(0).startsWith(keyPrefix), stack.getItem() + "'s description is " + keys);

        if (TestSupport.onNeoForge()) {
            List<String> full = TestSupport.fullTooltipKeys(helper, stack);
            helper.assertTrue(full.contains(keys.get(0)), stack.getItem() + "'s description is missing from its tooltip " + full);
        }
    }
}
