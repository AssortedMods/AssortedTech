package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.redstone.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class RedstoneLanguageProvider extends LibLanguageProvider {

    public RedstoneLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("block.assortedredstone.flip_flop_wall_torch", "Flip Flop Torch");
        this.add("block.assortedredstone.glowstone_wall_torch", "Glowstone Torch");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.redstone", "Redstone Torches");

        this.add("manual.assortedtech.chapter.redstone.glowstone_torch.title", "Glowstone Torch");
        this.add("manual.assortedtech.chapter.redstone.glowstone_torch",
                "A glowstone torch lights up when it is powered and goes dark when it is not, the way a "
                        + "redstone lamp does, but you can still walk through it like any torch.");

        this.add("manual.assortedtech.chapter.redstone.flip_flop_torch.title", "Flip Flop Torch");
        this.add("manual.assortedtech.chapter.redstone.flip_flop_torch",
                "A flip flop torch changes state each time it is powered and stays there. One button turns it "
                        + "on, the same button turns it off, which saves building the latch yourself.");
    }
}
