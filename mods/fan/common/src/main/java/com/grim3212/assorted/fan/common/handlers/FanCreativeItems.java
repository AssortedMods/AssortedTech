package com.grim3212.assorted.fan.common.handlers;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class FanCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(FanBlocks.FAN.get());

        return items.getItems();
    }

    public static void init() {
        // After the torches and before the alarm, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 300, FanCreativeItems::getCreativeItems);
    }
}
