package com.grim3212.assorted.sensors.common.item;

import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class SensorsItems {

    public static final IRegistryObject<GpsItem> GPS = register("gps", props -> new GpsItem(props.stacksTo(1).component(LibDataComponents.DESCRIPTION.get(), new ItemDescription(Component.translatable("tooltip.gps.usage").withStyle(ChatFormatting.GRAY)))));

    /** These register into {@link SensorsBlocks#ITEMS}, which is the mod's single item registry provider. */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        return SensorsBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    public static void init() {
    }
}
