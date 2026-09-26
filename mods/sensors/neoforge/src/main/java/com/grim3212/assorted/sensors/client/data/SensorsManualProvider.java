package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.Family;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. The sensors are read from the list they are registered from.
 */
public class SensorsManualProvider extends LibManualProvider {

    public SensorsManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        Block[] all = SensorsBlocks.SENSORS.stream().map(IRegistryObject::get).toArray(Block[]::new);

        ChapterBuilder sensors = this.chapter("sensors", 3);
        sensors.recipes("sensors", all).every(60).opens(all);
        sensors.text("triggers");
        sensors.recipes("gps", SensorsItems.GPS.get()).opens(SensorsItems.GPS.get());
        sensors.recipes("gps_sensor", SensorsBlocks.GPS_SENSOR.get()).opens(SensorsBlocks.GPS_SENSOR.get());
        sensors.recipes("upgraded_gps_sensor", SensorsBlocks.UPGRADED_GPS_SENSOR.get()).opens(SensorsBlocks.UPGRADED_GPS_SENSOR.get());
    }
}
