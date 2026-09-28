package com.grim3212.assorted.grates.client.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.Family;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * This part's chapter of the Assorted Tech section, which every part shares. It keeps the metal mesh's place in the
 * section's order.
 */
public class GratesManualProvider extends LibManualProvider {

    public GratesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        Block[] all = GratesBlocks.allItemGrates().stream().map(IRegistryObject::get).toArray(Block[]::new);

        ChapterBuilder grates = this.chapter("item_grates", 9);
        grates.recipes("item_grates", all).every(50).opens(all);
        grates.text("copper");
    }
}
