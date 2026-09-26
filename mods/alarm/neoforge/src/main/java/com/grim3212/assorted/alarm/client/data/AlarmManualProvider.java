package com.grim3212.assorted.alarm.client.data;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.Family;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.lib.data.LibManualProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class AlarmManualProvider extends LibManualProvider {

    public AlarmManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder alarm = this.chapter("alarm", 9);
        alarm.recipes("alarm", AlarmBlocks.ALARM.get()).opens(AlarmBlocks.ALARM.get());
    }
}
