package com.grim3212.assorted.fan.client.data;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class FanLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public FanLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("fan.screen", "Fan");
        this.add("fan.screen.ok", "Ok");
        this.add("fan.screen.cancel", "Cancel");
        this.add("fan.screen.mode", "Mode:");
        this.add("fan.screen.range", "Range:");
        this.add("fan.screen.mode.blow", "Blow");
        this.add("fan.screen.mode.suck", "Suck");
        this.add("fan.screen.mode.off", "Off");
        this.add("fan.screen.max", "Max");
        this.add("fan.screen.min", "Min");
        this.add("fan.screen.add_one", "+1");
        this.add("fan.screen.add_five", "+5");
        this.add("fan.screen.minus_one", "-1");
        this.add("fan.screen.minus_five", "-5");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.fan", "Fan");

        this.add("manual.assortedtech.chapter.fan.fan.title", "Fan");
        this.add("manual.assortedtech.chapter.fan.fan",
                "A fan blows entities away from itself or pulls them towards it. Right click it to set the "
                        + "range and which mode is set." + BREAK
                        + "Redstone turns a fan off rather than on, so it runs by default and a lever stops it.");
    }
}
