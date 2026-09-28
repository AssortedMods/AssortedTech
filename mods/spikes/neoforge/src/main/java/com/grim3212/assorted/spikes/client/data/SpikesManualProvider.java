package com.grim3212.assorted.spikes.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

/**
 * This part's chapter of the Assorted Tech section, which every part shares. The spikes are read from the list they
 * are registered from, so a new material lands on the page on its own.
 */
public class SpikesManualProvider extends LibManualProvider {

    public SpikesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        Block[] all = SpikesBlocks.SPIKES.stream().map(IRegistryObject::get).toArray(Block[]::new);

        ChapterBuilder spikes = this.chapter("spikes", 4);
        spikes.recipes("spikes", all).every(50).opens(all);
        spikes.text("materials");
    }
}
