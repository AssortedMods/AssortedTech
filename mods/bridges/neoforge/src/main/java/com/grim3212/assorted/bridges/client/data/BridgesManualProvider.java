package com.grim3212.assorted.bridges.client.data;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every block and item has to open a page, or the provider refuses to generate.
 */
public class BridgesManualProvider extends LibManualProvider {

    public BridgesManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder bridges = this.chapter("bridges", 1);

        // The beam itself is placed by the control, so it reads as the plain laser bridge.
        bridges.recipes("laser", BridgesBlocks.BRIDGE_CONTROL_LASER.get())
                .opens(BridgesBlocks.BRIDGE_CONTROL_LASER.get(), BridgesBlocks.BRIDGE.get());
        bridges.recipes("trick", BridgesBlocks.BRIDGE_CONTROL_TRICK.get()).opens(BridgesBlocks.BRIDGE_CONTROL_TRICK.get());
        bridges.recipes("accel", BridgesBlocks.BRIDGE_CONTROL_ACCEL.get()).opens(BridgesBlocks.BRIDGE_CONTROL_ACCEL.get());
        bridges.recipes("death", BridgesBlocks.BRIDGE_CONTROL_DEATH.get()).opens(BridgesBlocks.BRIDGE_CONTROL_DEATH.get());
        bridges.recipes("gravity", BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get()).opens(BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get());
    }
}
