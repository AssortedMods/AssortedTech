package com.grim3212.assorted.spikes.common.block;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.api.util.SpikeType;
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
import java.util.stream.Stream;

public class SpikesBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    /** One per {@link SpikeType}, as {@code <material>_spike}, in enum order. */
    public static final List<IRegistryObject<SpikeBlock>> SPIKES = Lists.newArrayList();

    static {
        Stream.of(SpikeType.values()).forEach((type) -> SPIKES.add(register(type)));
    }

    /** A spike and its item, whose fixed tooltip line is the spike's damage, which a block cannot add itself. */
    private static IRegistryObject<SpikeBlock> register(SpikeType type) {
        String name = type.toString() + "_spike";
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        IRegistryObject<SpikeBlock> ret = BLOCKS.register(name, () -> new SpikeBlock(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.METAL).sound(SoundType.METAL).noCollision().strength(1.5F, 10F), type));

        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        Component tooltip = Component.translatable("tooltip.spike.damage", Component.translatable(String.valueOf(type.getDamage())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
        ITEMS.register(name, () -> {
            Item.Properties props = new Item.Properties().useBlockDescriptionPrefix().setId(itemKey);
            if (type == SpikeType.NETHERITE) {
                props.fireResistant();
            }
            props.component(LibDataComponents.DESCRIPTION.get(), new ItemDescription(tooltip));
            return new BlockItem(ret.get(), props);
        });
        return ret;
    }

    public static void init() {
    }
}
