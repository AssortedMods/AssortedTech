package com.grim3212.assorted.alarm.common.handlers;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class AlarmCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(AlarmBlocks.ALARM.get());

        return items.getItems();
    }

    public static void init() {
        // After the fan and before the metal mesh, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 400, AlarmCreativeItems::getCreativeItems);
    }
}
