package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or item whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class GravityLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public GravityLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        // The gravity blocks report their range with the sensors' message, so Assorted Sensors writes the same line.
        this.add("message.sensor.range", "Sensor range update to: %s");

        // Families whose names read differently from their ids.
        this.nameBlocks("(.+)_directional", m -> "Directional " + titleCase(m.group(1)));

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.addGravityChapter();
    }

    private void addGravityChapter() {
        this.add("manual.assortedtech.chapter.gravity", "Gravity");

        this.add("manual.assortedtech.chapter.gravity.attractors.title", "Attractors");
        this.add("manual.assortedtech.chapter.gravity.attractors",
                "Powered, an attractor pulls everything within towards it." + BREAK
                        + "The directional version only pulls on the face the cone points out of.");

        this.add("manual.assortedtech.chapter.gravity.repulsors.title", "Repulsors");
        this.add("manual.assortedtech.chapter.gravity.repulsors",
                "A repulsor is an attractor run backwards. When powered, it pushes everything in range away." + BREAK
                        + "The directional version pushes only out of the face it points.");

        this.add("manual.assortedtech.chapter.gravity.gravitors.title", "Gravitors");
        this.add("manual.assortedtech.chapter.gravity.gravitors",
                "A gravitor lifts whatever is near it straight up while it is powered, and lets go the moment "
                        + "it stops. Whatever was in the air then falls the whole way." + BREAK
                        + "The directional version only lifts on the face it points out of.");

        this.add("manual.assortedtech.chapter.gravity.boots.title", "Gravity Boots");
        this.add("manual.assortedtech.chapter.gravity.boots",
                "Wearing gravity boots, none of the blocks in this chapter move you at all.");
    }
}
