package com.grim3212.assorted.elevators.common.handlers;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class ElevatorsCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    public static void init() {
        // Where the elevators sat in the tab when this was all one mod.
        SharedCreativeTabs.add(TAB, 600, ElevatorsCreativeItems::elevators);
    }

    private static List<ItemStack> elevators() {
        List<IRegistryObject<? extends Block>> blocks = List.of(ElevatorsBlocks.ELEVATOR, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR, ElevatorsBlocks.ELEVATOR_LANDING, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING, ElevatorsBlocks.INSTANT_ELEVATOR, ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR);
        return blocks.stream().map(block -> new ItemStack(block.get())).toList();
    }
}
