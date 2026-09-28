package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep
 * the section's order whichever parts are installed.
 */
public class GravityManualProvider extends LibManualProvider {

    public GravityManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        this.addGravity();
    }

    private void addGravity() {
        ChapterBuilder gravity = this.chapter("gravity", 2);

        gravity.recipes("attractors", GravityBlocks.ATTRACTOR.get(), GravityBlocks.ATTRACTOR_DIRECTIONAL.get()).every(60)
                .opens(GravityBlocks.ATTRACTOR.get(), GravityBlocks.ATTRACTOR_DIRECTIONAL.get());
        gravity.recipes("repulsors", GravityBlocks.REPULSOR.get(), GravityBlocks.REPULSOR_DIRECTIONAL.get()).every(60)
                .opens(GravityBlocks.REPULSOR.get(), GravityBlocks.REPULSOR_DIRECTIONAL.get());
        gravity.recipes("gravitors", GravityBlocks.GRAVITOR.get(), GravityBlocks.GRAVITOR_DIRECTIONAL.get()).every(60)
                .opens(GravityBlocks.GRAVITOR.get(), GravityBlocks.GRAVITOR_DIRECTIONAL.get());
        gravity.recipes("boots", GravityItems.GRAVITY_BOOTS.get()).opens(GravityItems.GRAVITY_BOOTS.get());
    }
}
