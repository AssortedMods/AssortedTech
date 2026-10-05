package com.grim3212.assorted.sensors.common.item;

import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class SensorsItems {

    public static final IRegistryObject<GpsItem> GPS = register("gps", props -> new GpsItem(props.stacksTo(1).component(LibDataComponents.DESCRIPTION.get(), new ItemDescription(Component.translatable("tooltip.gps.usage").withStyle(ChatFormatting.GRAY)))));

    /**
     * Since 1.21.2 every item has to know its own id before it is constructed, so the properties are
     * built here where the registration name is known. These still register into
     * {@link SensorsBlocks#ITEMS}, which is the mod's single item registry provider.
     */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return SensorsBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
