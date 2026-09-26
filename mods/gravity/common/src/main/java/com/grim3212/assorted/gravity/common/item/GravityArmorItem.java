package com.grim3212.assorted.gravity.common.item;

import com.grim3212.assorted.gravity.api.util.GravityArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

/** The gravity boots, a boots armour item of {@link GravityArmorMaterials#GRAVITY}. */
public class GravityArmorItem extends Item {

    public GravityArmorItem(Properties props) {
        super(props.humanoidArmor(GravityArmorMaterials.GRAVITY.material(), ArmorType.BOOTS));
    }
}
