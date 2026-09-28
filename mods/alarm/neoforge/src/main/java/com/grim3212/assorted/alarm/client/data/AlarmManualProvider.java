package com.grim3212.assorted.alarm.client.data;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class AlarmManualProvider extends LibManualProvider {

    public AlarmManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        ChapterBuilder alarm = this.chapter("alarm", 7);
        alarm.recipes("alarm", AlarmBlocks.ALARM.get()).opens(AlarmBlocks.ALARM.get());
    }
}
