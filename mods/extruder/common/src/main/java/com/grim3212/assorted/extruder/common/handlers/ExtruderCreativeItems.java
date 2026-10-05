package com.grim3212.assorted.extruder.common.handlers;

import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.creative.SharedCreativeTabs;
import com.grim3212.assorted.lib.family.Families;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** This part's share of the Assorted Tech tab, which every part asks for and the first to load registers. */
public class ExtruderCreativeItems {

    public static final ResourceKey<CreativeModeTab> TAB = Families.tab(Constants.FAMILY_ID);

    public static void init() {
        // Where the extruders sat in the tab when this was all one mod.
        SharedCreativeTabs.add(TAB, 800, ExtruderCreativeItems::extruders);
    }

    private static List<ItemStack> extruders() {
        CreativeTabItems items = new CreativeTabItems();
        for (ExtruderType type : ExtruderType.values()) {
            items.addIfObtainable(ExtruderItems.extruder(type), type.getPickaxes(), type.getShovels(), type.getAxes());
        }
        return items.getItems();
    }
}
