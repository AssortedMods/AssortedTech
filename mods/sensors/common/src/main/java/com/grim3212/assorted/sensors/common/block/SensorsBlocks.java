package com.grim3212.assorted.sensors.common.block;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.Family;
import com.grim3212.assorted.sensors.api.util.SensorType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SensorsBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Family.ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<GpsSensorBlock> GPS_SENSOR = register("gps_sensor", props -> new GpsSensorBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.STONE).strength(1.5F, 10.0F), false));
    public static final IRegistryObject<GpsSensorBlock> UPGRADED_GPS_SENSOR = register("upgraded_gps_sensor", props -> new GpsSensorBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.STONE).strength(3.0F, 12.0F), true));

    public static final List<IRegistryObject<SensorBlock>> SENSORS = Lists.newArrayList();

    static {
        Stream.of(SensorType.values()).forEach((type) -> SENSORS.add(register(type.toString() + "_sensor",
                props -> new SensorBlock(props.mapColor(type.getMapColor()).sound(type.getSoundType()).strength(1.0F, 10.0F), type),
                itemProps -> {
                },
                Component.translatable("tooltip.sensor.detects." + type.name().toLowerCase()).withStyle(ChatFormatting.GRAY))));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, itemProps -> {
        }, null);
    }

    /**
     * A block and its item. A non-null {@code tooltip} is the item's fixed line (the sensors'
     * "detects X"), which a block cannot add itself.
     */
    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory, Consumer<Item.Properties> itemProperties, Component tooltip) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        ITEMS.register(name, () -> {
            Item.Properties props = new Item.Properties().useBlockDescriptionPrefix().setId(key);
            itemProperties.accept(props);
            if (tooltip != null) {
                props.component(LibDataComponents.DESCRIPTION.get(), new ItemDescription(tooltip));
            }
            return new BlockItem(ret.get(), props);
        });
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    public static void init() {
    }
}
