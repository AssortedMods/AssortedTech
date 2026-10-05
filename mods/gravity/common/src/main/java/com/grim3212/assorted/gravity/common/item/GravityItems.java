package com.grim3212.assorted.gravity.common.item;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class GravityItems {

    public static final IRegistryObject<GravityArmorItem> GRAVITY_BOOTS = register("gravity_boots", GravityArmorItem::new);

    /**
     * Since 1.21.2 every item has to know its own id before it is constructed, so the properties are
     * built here where the registration name is known. These still register into
     * {@link GravityBlocks#ITEMS}, which is the mod's single item registry provider.
     */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return GravityBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
