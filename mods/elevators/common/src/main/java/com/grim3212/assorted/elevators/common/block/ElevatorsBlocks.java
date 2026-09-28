package com.grim3212.assorted.elevators.common.block;

import com.grim3212.assorted.elevators.Constants;
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
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class ElevatorsBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<ElevatorBlock> ELEVATOR = register("elevator", props -> new ElevatorBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops().pushReaction(PushReaction.BLOCK)));
    public static final IRegistryObject<CamouflagedElevatorBlock> CAMOUFLAGED_ELEVATOR = register("camouflaged_elevator", props -> new CamouflagedElevatorBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops().pushReaction(PushReaction.BLOCK)));
    public static final IRegistryObject<ElevatorLandingBlock> ELEVATOR_LANDING = register("elevator_landing", props -> new ElevatorLandingBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<CamouflagedElevatorLandingBlock> CAMOUFLAGED_ELEVATOR_LANDING = register("camouflaged_elevator_landing", props -> new CamouflagedElevatorLandingBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<InstantElevatorBlock> INSTANT_ELEVATOR = register("instant_elevator", props -> new InstantElevatorBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<CamouflagedInstantElevatorBlock> CAMOUFLAGED_INSTANT_ELEVATOR = register("camouflaged_instant_elevator", props -> new CamouflagedInstantElevatorBlock(props.mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

    /** A block and its item. */
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
