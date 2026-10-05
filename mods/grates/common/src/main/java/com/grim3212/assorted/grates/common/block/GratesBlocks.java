package com.grim3212.assorted.grates.common.block;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GratesBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID);

    /** One per {@link GrateMaterial}, as {@code <material>_item_grate}. */
    public static final Map<GrateMaterial, IRegistryObject<ItemGrateBlock>> ITEM_GRATES = registerItemGrates();
    /**
     * Copper comes in eight, as it does everywhere in vanilla: four oxidation stages, waxed and
     * unwaxed. Only the unwaxed ones weather; a waxed grate never changes, so it is a plain grate.
     */
    public static final WeatheringCopperCollection<IRegistryObject<ItemGrateBlock>> COPPER_ITEM_GRATES = registerCopperItemGrates();

    /** The iron grate is the metal mesh of Assorted Tech 9.x, and of Assorted Gravity before this mod. */
    private static final List<String> METAL_MESH_NAMESPACES = List.of(Constants.FAMILY_ID, "assortedgravity");

    static {
        Identifier ironGrate = ITEM_GRATES.get(GrateMaterial.IRON).getId();
        for (String namespace : METAL_MESH_NAMESPACES) {
            Identifier metalMesh = Identifier.fromNamespaceAndPath(namespace, "metal_mesh");
            Services.REGISTRY_FACTORY.alias(Registries.BLOCK, metalMesh, ironGrate);
            Services.REGISTRY_FACTORY.alias(Registries.ITEM, metalMesh, ironGrate);
        }
    }

    private static Map<GrateMaterial, IRegistryObject<ItemGrateBlock>> registerItemGrates() {
        Map<GrateMaterial, IRegistryObject<ItemGrateBlock>> grates = new EnumMap<>(GrateMaterial.class);
        for (GrateMaterial material : GrateMaterial.values()) {
            grates.put(material, register(material + "_item_grate", props -> new ItemGrateBlock(itemGrate(props).mapColor(material.getMapColor()).sound(SoundType.METAL))));
        }
        return grates;
    }

    private static WeatheringCopperCollection<IRegistryObject<ItemGrateBlock>> registerCopperItemGrates() {
        WeatheringCopperCollection<String> names = WeatheringCopperCollection.prefixWithState(WeatheringCopperCollection.create("copper_item_grate"));
        return names.apply(
                weathering -> WeatheringCopperCollection.zipMap(WeatheringCopperCollection.STATES, weathering,
                        (age, name) -> register(name, props -> new WeatheringItemGrateBlock(age, copperItemGrate(props, age, age != WeatherState.OXIDIZED)))),
                waxed -> WeatheringCopperCollection.zipMap(WeatheringCopperCollection.STATES, waxed,
                        (age, name) -> register(name, props -> new ItemGrateBlock(copperItemGrate(props, age, false)))));
    }

    /**
     * A copper grate sounds like vanilla's copper grate and colours like its copper of the same
     * stage. Only a grate with a stage left to reach is randomly ticked.
     */
    private static BlockBehaviour.Properties copperItemGrate(BlockBehaviour.Properties props, WeatherState age, boolean weathers) {
        if (weathers) {
            props.randomTicks();
        }

        return itemGrate(props).sound(SoundType.COPPER_GRATE).mapColor(switch (age) {
            case UNAFFECTED -> MapColor.COLOR_ORANGE;
            case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case WEATHERED -> MapColor.WARPED_STEM;
            case OXIDIZED -> MapColor.WARPED_NYLIUM;
        });
    }

    private static BlockBehaviour.Properties itemGrate(BlockBehaviour.Properties props) {
        return props.strength(0.4F, 1.0F).requiresCorrectToolForDrops().noOcclusion().isSuffocating(GratesBlocks::never).isViewBlocking(GratesBlocks::never).isRedstoneConductor(GratesBlocks::never);
    }

    /** A block and its item. */
    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Every block has to know its own id before it is constructed, so the properties are built here.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        IRegistryObject<T> ret = BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));
        return ret;
    }

    /** Every item grate: iron, the eight copper, gold, then the Assorted Core metals. */
    public static List<IRegistryObject<ItemGrateBlock>> allItemGrates() {
        List<IRegistryObject<ItemGrateBlock>> grates = new ArrayList<>();
        grates.add(ITEM_GRATES.get(GrateMaterial.IRON));
        COPPER_ITEM_GRATES.forEach(grates::add);
        ITEM_GRATES.forEach((material, grate) -> {
            if (material != GrateMaterial.IRON) {
                grates.add(grate);
            }
        });
        return grates;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    public static void init() {
    }
}
