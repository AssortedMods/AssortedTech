package com.grim3212.assorted.gravity.api.util;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

/**
 * The mod's armour materials, a registry on 1.21.1. Each one's worn texture is
 * {@code assets/assortedgravity/textures/models/armor/<name>_layer_1.png} (and {@code _layer_2.png} for leggings),
 * or the worn armour renders untextured.
 */
public final class GravityArmorMaterials {

    public static final RegistryProvider<ArmorMaterial> ARMOR_MATERIALS = RegistryProvider.create(Registries.ARMOR_MATERIAL, Constants.MOD_ID);

    /** The durability multiplier, which on 1.21.1 goes on each piece's properties rather than the material. */
    public static final int GRAVITY_DURABILITY = 15;

    public static final IRegistryObject<ArmorMaterial> GRAVITY = ARMOR_MATERIALS.register("gravity", () -> new ArmorMaterial(defense(2, 6, 5, 2), 9,
            SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(LibCommonTags.Items.ENDER_PEARLS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "gravity"))), 0.0F, 0.0F));

    private GravityArmorMaterials() {
    }

    private static EnumMap<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.LEGGINGS, leggings);
        defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
        defense.put(ArmorItem.Type.HELMET, helmet);
        return defense;
    }

    public static void init() {
    }
}
