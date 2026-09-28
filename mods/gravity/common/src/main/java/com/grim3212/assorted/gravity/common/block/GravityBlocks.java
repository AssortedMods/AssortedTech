package com.grim3212.assorted.gravity.common.block;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.api.util.GravityType;
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

public class GravityBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<GravityBlock> ATTRACTOR = register("attractor", props -> new GravityBlock(GravityType.ATTRACT, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<GravityBlock> REPULSOR = register("repulsor", props -> new GravityBlock(GravityType.REPULSE, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<GravityBlock> GRAVITOR = register("gravitor", props -> new GravityBlock(GravityType.GRAVITATE, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));

    public static final IRegistryObject<GravityDirectionalBlock> ATTRACTOR_DIRECTIONAL = register("attractor_directional", props -> new GravityDirectionalBlock(GravityType.ATTRACT, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<GravityDirectionalBlock> REPULSOR_DIRECTIONAL = register("repulsor_directional", props -> new GravityDirectionalBlock(GravityType.REPULSE, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<GravityDirectionalBlock> GRAVITOR_DIRECTIONAL = register("gravitor_directional", props -> new GravityDirectionalBlock(GravityType.GRAVITATE, props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.3F, 10.0F).requiresCorrectToolForDrops()));

    /** A block and its item. */
    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        IRegistryObject<T> ret = BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));
        return ret;
    }

    public static void init() {
    }
}
