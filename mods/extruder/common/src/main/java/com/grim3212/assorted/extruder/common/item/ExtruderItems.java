package com.grim3212.assorted.extruder.common.item;

import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.Family;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class ExtruderItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    /** One per material, as {@code <material>_extruder}, in {@link ExtruderType} order. */
    public static final Map<ExtruderType, IRegistryObject<ExtruderItem>> EXTRUDERS = new EnumMap<>(ExtruderType.class);

    /** Since 1.21.2 every item has to know its own id before it is constructed, so the properties are built here. */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    static {
        for (ExtruderType type : ExtruderType.values()) {
            // Its level shown as Assorted Storage shows a storage level.
            ItemDescription level = new ItemDescription(Component.translatable("tooltip.extruder.level", Component.literal(String.valueOf(type.getLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
            EXTRUDERS.put(type, register(type + "_extruder", props -> {
                props.stacksTo(1).component(LibDataComponents.DESCRIPTION.get(), level);
                return new ExtruderItem(type == ExtruderType.NETHERITE ? props.fireResistant() : props, type);
            }));
        }
    }

    public static ExtruderItem extruder(ExtruderType type) {
        return EXTRUDERS.get(type).get();
    }

    public static void init() {
    }
}
