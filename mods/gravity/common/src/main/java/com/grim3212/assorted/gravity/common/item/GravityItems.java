package com.grim3212.assorted.gravity.common.item;

import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class GravityItems {

    public static final IRegistryObject<GravityArmorItem> GRAVITY_BOOTS = register("gravity_boots", GravityArmorItem::new);

    /** These register into {@link GravityBlocks#ITEMS}, which is the mod's single item registry provider. */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        return GravityBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    public static void init() {
    }
}
