package com.grim3212.assorted.gravity.common.handlers;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class GravityCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    private static List<ItemStack> getGravityItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(GravityItems.GRAVITY_BOOTS.get());

        items.add(GravityBlocks.ATTRACTOR.get());
        items.add(GravityBlocks.ATTRACTOR_DIRECTIONAL.get());
        items.add(GravityBlocks.REPULSOR.get());
        items.add(GravityBlocks.REPULSOR_DIRECTIONAL.get());
        items.add(GravityBlocks.GRAVITOR.get());
        items.add(GravityBlocks.GRAVITOR_DIRECTIONAL.get());

        return items.getItems();
    }

    public static void init() {
        // First, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 100, GravityCreativeItems::getGravityItems);
    }
}
