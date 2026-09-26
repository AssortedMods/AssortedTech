package com.grim3212.assorted.sensors.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.sensors.Family;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class SensorsCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(SensorsItems.GPS.get());
        items.add(SensorsBlocks.GPS_SENSOR.get());
        items.add(SensorsBlocks.UPGRADED_GPS_SENSOR.get());

        SensorsBlocks.SENSORS.forEach((sensor) -> {
            items.add(sensor.get());
        });

        return items.getItems();
    }

    public static void init() {
        // After the extruders and before the spikes, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 900, SensorsCreativeItems::getCreativeItems);
    }
}
