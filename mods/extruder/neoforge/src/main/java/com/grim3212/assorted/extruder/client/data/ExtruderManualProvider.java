package com.grim3212.assorted.extruder.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.Family;
import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every item has to open a page, or the provider refuses to generate.
 */
public class ExtruderManualProvider extends LibManualProvider {

    public ExtruderManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        Item[] all = ExtruderItems.EXTRUDERS.values().stream().map(IRegistryObject::get).toArray(Item[]::new);

        ChapterBuilder extruder = this.chapter("extruder", 8);
        extruder.recipes("extruder", all).every(50).opens(all).opens(ExtruderEntities.EXTRUDER.get());
        extruder.text("materials");
    }
}
