package com.grim3212.assorted.bridges.common.handlers;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class BridgesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(BridgesBlocks.BRIDGE_CONTROL_ACCEL.get());
        items.add(BridgesBlocks.BRIDGE_CONTROL_DEATH.get());
        items.add(BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get());
        items.add(BridgesBlocks.BRIDGE_CONTROL_LASER.get());
        items.add(BridgesBlocks.BRIDGE_CONTROL_TRICK.get());

        return items.getItems();
    }

    public static void init() {
        // After the elevators and before the extruders, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 700, BridgesCreativeItems::getCreativeItems);
    }
}
