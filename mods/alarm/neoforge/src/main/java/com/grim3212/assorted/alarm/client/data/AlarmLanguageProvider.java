package com.grim3212.assorted.alarm.client.data;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. The alarm is named here because its name reads differently from its id (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class AlarmLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public AlarmLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("block.assortedalarm.alarm", "Alarm Box");

        this.add("assortedalarm.subtitle.alarm", "Alarm going off");

        this.add("alarm.screen", "Alarm Box");
        this.add("alarm.screen.done", "Done");
        this.add("alarm.screen.description", "Press the button below to cycle through the different types of alarms available. When you've found the one you want, click the 'Done' button at the bottom. Supply a redstone pulse (on/off signal) to any side of the box to sound the alarm.");
        this.add("alarm.screen.name", "Alarm %s");
        this.add("alarm.screen.test", "Test");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.add("manual.assortedtech.chapter.alarm", "Alarm");

        this.add("manual.assortedtech.chapter.alarm.alarm.title", "Alarm");
        this.add("manual.assortedtech.chapter.alarm.alarm",
                "An alarm sounds while it has power, and goes on any face of a block. Right click it to pick "
                        + "which of the sounds it makes." + BREAK
                        + "Wired to a sensor it becomes a doorbell, or something considerably less polite.");
    }
}
