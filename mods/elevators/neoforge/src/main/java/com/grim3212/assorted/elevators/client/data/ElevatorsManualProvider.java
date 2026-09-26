package com.grim3212.assorted.elevators.client.data;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.Family;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.entity.ElevatorsEntities;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every block and item has to open a page, or the provider refuses to generate.
 */
public class ElevatorsManualProvider extends LibManualProvider {

    public ElevatorsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder elevators = this.chapter("elevators", 6);
        elevators.recipes("elevator", ElevatorsBlocks.ELEVATOR.get()).opens(ElevatorsBlocks.ELEVATOR.get()).opens(ElevatorsEntities.ELEVATOR_CAR.get());
        elevators.recipes("camouflaged_elevator", ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()).opens(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        elevators.recipes("elevator_landing", ElevatorsBlocks.ELEVATOR_LANDING.get()).opens(ElevatorsBlocks.ELEVATOR_LANDING.get());
        elevators.recipes("camouflaged_elevator_landing", ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()).opens(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get());
        elevators.recipes("instant_elevator", ElevatorsBlocks.INSTANT_ELEVATOR.get()).opens(ElevatorsBlocks.INSTANT_ELEVATOR.get());
        elevators.recipes("camouflaged_instant_elevator", ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get()).opens(ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get());
    }
}
