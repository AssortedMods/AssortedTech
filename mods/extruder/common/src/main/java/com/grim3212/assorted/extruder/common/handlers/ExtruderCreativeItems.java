package com.grim3212.assorted.extruder.common.handlers;

import com.grim3212.assorted.extruder.Family;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class ExtruderCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    public static void init() {
        // Where the extruders sat in the tab when this was all one mod.
        SharedCreativeTabs.add(TAB, 800, ExtruderCreativeItems::extruders);
    }

    private static List<ItemStack> extruders() {
        return Arrays.stream(ExtruderType.values()).map(type -> new ItemStack(ExtruderItems.extruder(type))).toList();
    }
}
