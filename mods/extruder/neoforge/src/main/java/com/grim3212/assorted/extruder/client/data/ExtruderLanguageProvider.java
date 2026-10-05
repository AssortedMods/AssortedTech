package com.grim3212.assorted.extruder.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.api.ExtruderTags;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.data.ExtruderItemTagProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class ExtruderLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public ExtruderLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("extruder.screen.fuel", "Fuel: %s");
        this.add("extruder.screen.mined", "Mined");
        this.add("tooltip.extruder.level", "Extruder Level %s");
        this.add("message.extruder.started", "Extruder started");
        this.add("message.extruder.no_fuel", "The extruder has no fuel");
        this.add("message.extruder.stopped", "Extruder stopped");
        this.add("extruder.screen.mine_blocks", "Mine: %s");
        this.add("extruder.screen.extrude_blocks", "Extrude: %s");
        this.add("extruder.screen.start", "Start");
        this.add("extruder.screen.stop", "Stop");
        this.add("extruder.screen.direction.down", "Down");
        this.add("extruder.screen.direction.up", "Up");
        this.add("extruder.screen.direction.north", "North");
        this.add("extruder.screen.direction.south", "South");
        this.add("extruder.screen.direction.west", "West");
        this.add("extruder.screen.direction.east", "East");

        for (int level = 0; level < ExtruderTags.Items.EXTRUDER_LEVELS; level++) {
            this.add("tag.item." + Constants.MOD_ID + ".extruders.level_" + level, "Level " + level + " Extruders");
        }
        // The c: tool tags this mod fills; Assorted Tools names the same ones, and the rest.
        for (ExtruderType type : ExtruderItemTagProvider.vanillaToolMaterials()) {
            for (String kind : new String[]{"pickaxes", "shovels", "axes"}) {
                this.add("tag.item.c." + kind + "." + type, titleCase(type.toString()) + " " + titleCase(kind));
            }
        }

        this.nameItems("(.+)_extruder", m -> titleCase(m.group(1)) + " Extruder");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.extruder", "Extruder");

        this.add("manual.assortedtech.chapter.extruder.extruder.title", "Extruder");
        this.add("manual.assortedtech.chapter.extruder.extruder",
                "Right click it to fuel it, choose its direction and start/stop it. You can also punch it with an empty hand to start or "
                        + "stop it." + BREAK
                        + "It will move in a straight line, mining whatever the material it is made out of can mine, and lays a block from its top slots behind itself at every step.");

        this.add("manual.assortedtech.chapter.extruder.materials.title", "Materials");
        this.add("manual.assortedtech.chapter.extruder.materials",
                "An extruder is made from a pickaxe, shovel and axe of one material, and mines only what "
                        + "those tools can mine. There is one for every material that Assorted Core supports." + BREAK
                        + "Upgraded extruders require an extruder of the previous level surrounded by its tools. "
                        + "Each level is faster, more efficient and has more inventory slots than the previous level.");
    }
}
