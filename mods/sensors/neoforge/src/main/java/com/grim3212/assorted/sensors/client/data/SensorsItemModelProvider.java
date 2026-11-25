package com.grim3212.assorted.sensors.client.data;

import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Item models for the items not drawn from a block model, which is only the GPS. */
public class SensorsItemModelProvider extends ItemModelProvider {

    public SensorsItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Sensors item models";
    }

    @Override
    protected void registerModels() {
        this.basicItem(SensorsItems.GPS.get());
    }
}
