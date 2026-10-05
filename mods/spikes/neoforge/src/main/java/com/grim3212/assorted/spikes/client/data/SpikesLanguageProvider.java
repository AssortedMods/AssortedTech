package com.grim3212.assorted.spikes.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.spikes.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. The spikes read differently from their ids, so they are named by a pattern
 * here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class SpikesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public SpikesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("death.attack.assortedspikes.spike", "%1$s was pierced by spikes");
        this.add("death.attack.assortedspikes.spike.player", "%1$s walked into a spike whilst trying to escape %2$s");

        this.add("assortedspikes.subtitle.spike_deploy", "Spike deployed");
        this.add("assortedspikes.subtitle.spike_close", "Spike closed");

        this.add("tooltip.spike.damage", "Damage: %s");

        this.add("tag.item.assortedspikes.spikes", "Spikes");

        this.nameBlocks("(.+)_spike", m -> titleCase(m.group(1)) + " Spikes");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.spikes", "Spikes");

        this.add("manual.assortedtech.chapter.spikes.spikes.title", "Spikes");
        this.add("manual.assortedtech.chapter.spikes.spikes",
                "A spike goes on any face of a block, floor, wall or ceiling, and hurts whatever touches it when powered." + BREAK
                        + "An unpowered spike is flush against the ground and does nothing.");

        this.add("manual.assortedtech.chapter.spikes.materials.title", "Materials");
        this.add("manual.assortedtech.chapter.spikes.materials",
                "What a spike is made of decides how hard it hits. We support many Vanilla materials and every material Assorted Core adds.");
    }
}
