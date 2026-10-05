package com.grim3212.assorted.redstone.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class RedstoneCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(RedstoneItems.FLIP_FLOP_TORCH.get());
        items.add(RedstoneItems.GLOWSTONE_TORCH.get());

        return items.getItems();
    }

    public static void init() {
        // After the gravity blocks and before the fan, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 200, RedstoneCreativeItems::getCreativeItems);
    }
}
