package com.grim3212.assorted.fan.client.data;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class FanManualProvider extends LibManualProvider {

    public FanManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder fan = this.chapter("fan", 6);
        fan.recipes("fan", FanBlocks.FAN.get()).opens(FanBlocks.FAN.get());
    }
}
