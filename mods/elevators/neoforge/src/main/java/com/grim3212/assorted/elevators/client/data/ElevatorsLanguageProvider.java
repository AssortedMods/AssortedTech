package com.grim3212.assorted.elevators.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.elevators.Constants;
import net.minecraft.data.PackOutput;

/**
 * Generates the en_us.json of this mod. A block or entity whose name is its id in title case needs no line here (see
 * {@link LibLanguageProvider}); the manual's keys are the Assorted Tech section's, which every part shares.
 */
public class ElevatorsLanguageProvider extends LibLanguageProvider {

    public ElevatorsLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedtech", "Assorted Tech");

        this.add("death.attack.assortedelevators.elevator", "%1$s was squashed by an elevator");
        this.add("death.attack.assortedelevators.elevator.player", "%1$s was squashed by an elevator whilst trying to escape %2$s");

        this.addManual();
    }

    /** This part's chapter of the Assorted Tech section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assortedtech.title", "Assorted Tech");
        this.add("manual.assortedtech.description",
                "Laser bridges, gravity, sensors, spikes and additional redstone fun.");

        this.addElevatorsChapter();
    }

    private void addElevatorsChapter() {
        this.add("manual.assortedtech.chapter.elevators", "Elevators");

        this.add("manual.assortedtech.chapter.elevators.elevator.title", "Elevator");
        this.add("manual.assortedtech.chapter.elevators.elevator",
                "A moving platform that can go up and down from stop to stop, carrying anything standing on it and hurting anything it comes down on. Place up to 3 by 3 side by side and they travel together. Stand on it and jump to go up or sneak to go down. It stops at every landing, and at the ends of the shaft which is the bottom, or two blocks under the ceiling.");

        this.add("manual.assortedtech.chapter.elevators.camouflaged_elevator.title", "Camouflaged Elevator");
        this.add("manual.assortedtech.chapter.elevators.camouflaged_elevator",
                "An elevator that can look like any solid block, and keeps its look from stop to stop. Right click a full block on it to disguise it and sneak and use it with an empty hand to take the disguise off. The block is not used up.");

        this.add("manual.assortedtech.chapter.elevators.elevator_landing.title", "Elevator Landing");
        this.add("manual.assortedtech.chapter.elevators.elevator_landing",
                "Build one into the side of the shaft, one block above a floor, to make that floor a stop. A redstone pulse on the block will call the elevator to that floor. A comparator reading it gives 15 while the car is there.");

        this.add("manual.assortedtech.chapter.elevators.camouflaged_elevator_landing.title", "Camouflaged Elevator Landing");
        this.add("manual.assortedtech.chapter.elevators.camouflaged_elevator_landing",
                "An elevator landing that can be disguised as the wall it is built into, the same way as a camouflaged elevator. It still calls the car and still tells a comparator when the car is there.");

        this.add("manual.assortedtech.chapter.elevators.instant_elevator.title", "Instant Elevator");
        this.add("manual.assortedtech.chapter.elevators.instant_elevator",
                "Stand on one and jump to be taken straight to the next instant elevator of the same color above, or sneak to go to the next one below. Hold jump to keep going up floor after floor. Dye one to change its color so you can go to different levels in the same column. There has to be room to stand on the one you are sent to.");

        this.add("manual.assortedtech.chapter.elevators.camouflaged_instant_elevator.title", "Camouflaged Instant Elevator");
        this.add("manual.assortedtech.chapter.elevators.camouflaged_instant_elevator",
                "An instant elevator that can look like any solid block. Use a block on it to disguise it and sneak and use it with an empty hand to take the disguise off. The block is not used up. Dye it like any other instant elevator.");
    }
}
