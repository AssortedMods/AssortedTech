package com.grim3212.assorted.gravity.common.handlers;

import com.grim3212.assorted.gravity.Family;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class GravityCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

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

    private static List<ItemStack> getMetalMeshItems() {
        CreativeTabItems items = new CreativeTabItems();
        items.add(GravityBlocks.METAL_MESH.get());
        return items.getItems();
    }

    public static void init() {
        // Two slots, as the tab was when this was all one mod: the gravity blocks first and the mesh after the alarm.
        SharedCreativeTabs.add(TAB, 100, GravityCreativeItems::getGravityItems);
        SharedCreativeTabs.add(TAB, 500, GravityCreativeItems::getMetalMeshItems);
    }
}
