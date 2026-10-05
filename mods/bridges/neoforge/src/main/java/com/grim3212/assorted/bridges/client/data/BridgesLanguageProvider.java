package com.grim3212.assorted.bridges.client.data;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class BridgesLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public BridgesLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("death.attack.assortedbridges.laser", "%1$s played with lasers");
        this.add("death.attack.assortedbridges.laser.player", "%1$s got zapped by a laser whilst trying to escape %2$s");

        this.add("block.assortedbridges.bridge", "Bridge Piece");
        this.add("block.assortedbridges.bridge_control_gravity", "Gravity Lift Control");

        this.add("tag.item.assortedbridges.gravity_bridge_immune", "Immune to Gravity Bridges");

        // Families whose names read differently from their ids.
        this.nameBlocks("bridge_control_(laser|accel|death|trick)", m -> titleCase(m.group(1)) + " Bridge Control");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.addBridgesChapter();
    }

    private void addBridgesChapter() {
        this.add("manual.assortedtech.chapter.bridges", "Laser Bridges");

        this.add("manual.assortedtech.chapter.bridges.laser.title", "Laser Bridge");
        this.add("manual.assortedtech.chapter.bridges.laser",
                "A bridge control shoots a beam when it is given redstone, and the beam is solid ground. It "
                        + "cannot be broken while it is lit, and it stops at the first block in the way." + BREAK
                        + "Right click the control with a block to give the beam that block's texture, and shift "
                        + "right click with an empty hand to put it back.");

        this.add("manual.assortedtech.chapter.bridges.trick.title", "Trick Bridge");
        this.add("manual.assortedtech.chapter.bridges.trick",
                "A trick bridge is a laser bridge you fall straight through. It looks solid but definiitely is not.");

        this.add("manual.assortedtech.chapter.bridges.accel.title", "Acceleration Bridge");
        this.add("manual.assortedtech.chapter.bridges.accel",
                "An acceleration bridge carries you along faster than walking. Useful for long crossings.");

        this.add("manual.assortedtech.chapter.bridges.death.title", "Death Bridge");
        this.add("manual.assortedtech.chapter.bridges.death",
                "A death bridge looks like a trick bridge and hurts anything that touches the beam.");

        this.add("manual.assortedtech.chapter.bridges.gravity.title", "Gravity Lift");
        this.add("manual.assortedtech.chapter.bridges.gravity",
                "A gravity lift is a beam you step into rather than onto. The lift will push whatever is inside it the way it points.");
    }
}
