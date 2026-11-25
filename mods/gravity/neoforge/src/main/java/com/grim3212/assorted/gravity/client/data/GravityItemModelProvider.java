package com.grim3212.assorted.gravity.client.data;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Item models for the items not drawn from a block model, which is only the gravity boots. */
public class GravityItemModelProvider extends ItemModelProvider {

    public GravityItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Gravity item models";
    }

    @Override
    protected void registerModels() {
        this.basicItem(GravityItems.GRAVITY_BOOTS.get());
    }
}
