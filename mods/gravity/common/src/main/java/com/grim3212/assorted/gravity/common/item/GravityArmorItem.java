package com.grim3212.assorted.gravity.common.item;

import com.grim3212.assorted.gravity.api.util.GravityArmorMaterials;
import net.minecraft.world.item.ArmorItem;

/** The gravity boots, a boots armour item of {@link GravityArmorMaterials#GRAVITY}. */
public class GravityArmorItem extends ArmorItem {

    public GravityArmorItem(Properties props) {
        super(GravityArmorMaterials.GRAVITY.asHolder(), ArmorItem.Type.BOOTS, props.durability(ArmorItem.Type.BOOTS.getDurability(GravityArmorMaterials.GRAVITY_DURABILITY)));
    }
}
