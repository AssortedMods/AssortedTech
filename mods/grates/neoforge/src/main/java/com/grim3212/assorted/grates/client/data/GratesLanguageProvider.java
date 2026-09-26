package com.grim3212.assorted.grates.client.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. Every grate's name is its id in title case, so none needs a line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class GratesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public GratesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("tag.item.assortedgrates.item_grates", "Item Grates");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.item_grates", "Item Grates");

        this.add("manual.assortedtech.chapter.item_grates.item_grates.title", "Item Grates");
        this.add("manual.assortedtech.chapter.item_grates.item_grates",
                "A grate you can walk across that items drop straight through. Players and mobs stand on it but any item dropped on it falls through." + BREAK
                        + "There is one for iron, gold, copper and every metal Assorted Core adds.");

        this.add("manual.assortedtech.chapter.item_grates.copper.title", "Copper Item Grates");
        this.add("manual.assortedtech.chapter.item_grates.copper",
                "A copper grate weathers where it stands, through exposed, weathered and oxidized, like any other copper." + BREAK
                        + "An axe scrapes a stage back off it, and a honeycomb waxes it to hold the stage it is at.");
    }
}
