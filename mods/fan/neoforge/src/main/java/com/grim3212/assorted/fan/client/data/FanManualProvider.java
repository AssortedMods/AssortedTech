package com.grim3212.assorted.fan.client.data;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.Family;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class FanManualProvider extends LibManualProvider {

    public FanManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder fan = this.chapter("fan", 8);
        fan.recipes("fan", FanBlocks.FAN.get()).opens(FanBlocks.FAN.get());
    }
}
