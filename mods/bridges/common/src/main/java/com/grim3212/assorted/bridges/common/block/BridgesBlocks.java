package com.grim3212.assorted.bridges.common.block;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.api.util.BridgeType;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class BridgesBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<BridgeBlock> BRIDGE = register("bridge", props -> new BridgeBlock(props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).dynamicShape().noOcclusion().noLootTable().isValidSpawn((s, g, p, e) -> false)));
    public static final IRegistryObject<BridgeControlBlock> BRIDGE_CONTROL_LASER = register("bridge_control_laser", props -> new BridgeControlBlock(BridgeType.LASER, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1.0F, 1.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<BridgeControlBlock> BRIDGE_CONTROL_ACCEL = register("bridge_control_accel", props -> new BridgeControlBlock(BridgeType.ACCEL, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1.0F, 1.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<BridgeControlBlock> BRIDGE_CONTROL_TRICK = register("bridge_control_trick", props -> new BridgeControlBlock(BridgeType.TRICK, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1.0F, 1.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<BridgeControlBlock> BRIDGE_CONTROL_DEATH = register("bridge_control_death", props -> new BridgeControlBlock(BridgeType.DEATH, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1.0F, 1.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<BridgeControlBlock> BRIDGE_CONTROL_GRAVITY = register("bridge_control_gravity", props -> new BridgeControlBlock(BridgeType.GRAVITY, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(1.0F, 1.0F).requiresCorrectToolForDrops()));

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
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
