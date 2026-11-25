package com.grim3212.assorted.sensors.gametest;

import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.fullTooltipKeys;
import static com.grim3212.assorted.lib.test.TestSupport.onNeoForge;
import static com.grim3212.assorted.lib.test.TestSupport.tooltipKeys;

/**
 * Item tooltip lines, which come from data components. NeoForge also builds the full tooltip on the
 * server, so there it is checked too.
 */
final class TooltipTests {

    private TooltipTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("sensor_tooltips_describe_them", TooltipTests::sensorTooltipsDescribeThem);
    }

    /** Every sensor names what it detects, in an {@code assortedlib:description}. */
    private static void sensorTooltipsDescribeThem(GameTestHelper helper) {
        for (IRegistryObject<? extends Block> sensor : SensorsBlocks.SENSORS) {
            assertDescribed(helper, new ItemStack(sensor.get()), "tooltip.sensor.detects.");
        }
        helper.succeed();
    }

    /** The stack's description is one line whose key starts with {@code keyPrefix}. */
    private static void assertDescribed(GameTestHelper helper, ItemStack stack, String keyPrefix) {
        List<String> keys = tooltipKeys(helper, stack, LibDataComponents.DESCRIPTION.get());
        helper.assertTrue(keys.size() == 1 && keys.get(0).startsWith(keyPrefix), stack.getItem() + "'s description is " + keys);

        if (onNeoForge()) {
            List<String> full = fullTooltipKeys(helper, stack);
            helper.assertTrue(full.contains(keys.get(0)), stack.getItem() + "'s description is missing from its tooltip " + full);
        }
    }
}
