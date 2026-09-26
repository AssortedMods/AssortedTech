package com.grim3212.assorted.spikes.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.spikes.Family;
import com.grim3212.assorted.spikes.SpikesCommonMod;
import com.grim3212.assorted.spikes.api.util.SpikeType;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class SpikesCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = SharedCreativeTabs.tab(Identifier.fromNamespaceAndPath(Family.ID, "tab"), Family.ICONS);

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        SpikesBlocks.SPIKES.forEach((spike) -> {
            if (canNotCraft(spike.get().getSpikeType())) {
                return;
            }

            items.add(spike.get());
        });

        return items.getItems();
    }

    private static boolean canNotCraft(SpikeType type) {
        return SpikesCommonMod.COMMON_CONFIG.hideUncraftableItems.get() && type.isUncraftable();
    }

    public static void init() {
        // Last, after the sensors, as the tab was when this was all one mod.
        SharedCreativeTabs.add(TAB, 1100, SpikesCreativeItems::getCreativeItems);
    }
}
