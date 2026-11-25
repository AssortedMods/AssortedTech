package com.grim3212.assorted.redstone.common.item;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;

import java.util.function.Function;

public class RedstoneItems {

    public static final IRegistryObject<StandingAndWallBlockItem> FLIP_FLOP_TORCH = register("flip_flop_torch", props -> new StandingAndWallBlockItem(RedstoneBlocks.FLIP_FLOP_TORCH.get(), RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get(), props, Direction.DOWN));
    public static final IRegistryObject<StandingAndWallBlockItem> GLOWSTONE_TORCH = register("glowstone_torch", props -> new StandingAndWallBlockItem(RedstoneBlocks.GLOWSTONE_TORCH.get(), RedstoneBlocks.GLOWSTONE_WALL_TORCH.get(), props, Direction.DOWN));

    /** These register into {@link RedstoneBlocks#ITEMS}. */
    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        return RedstoneBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    public static void init() {
    }
}
