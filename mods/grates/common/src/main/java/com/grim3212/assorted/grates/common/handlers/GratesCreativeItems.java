package com.grim3212.assorted.grates.common.handlers;

import com.grim3212.assorted.grates.Family;
import com.grim3212.assorted.grates.GratesCommonMod;
import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class GratesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(GratesBlocks.ITEM_GRATES.get(GrateMaterial.IRON).get());
        GratesBlocks.COPPER_ITEM_GRATES.forEach(grate -> items.add(grate.get()));
        GratesBlocks.ITEM_GRATES.forEach((material, grate) -> {
            if (material == GrateMaterial.IRON || canNotCraft(material)) {
                return;
            }

            items.add(grate.get());
        });

        return items.getItems();
    }

    private static boolean canNotCraft(GrateMaterial material) {
        return GratesCommonMod.COMMON_CONFIG.hideUncraftableItems.get() && material.isUncraftable();
    }

    public static void init() {
        // After the alarm, where the metal mesh was when this was all one mod.
        SharedCreativeTabs.add(TAB, 500, GratesCreativeItems::getCreativeItems);
    }
}
