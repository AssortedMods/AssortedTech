package com.grim3212.assorted.redstone.common.block;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.redstone.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class RedstoneBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<FlipFlopTorchBlock> FLIP_FLOP_TORCH = registerNoItem("flip_flop_torch", props -> new FlipFlopTorchBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().lightLevel(litBlockEmission(7)).sound(SoundType.WOOD)));
    public static final IRegistryObject<FlipFlopWallTorchBlock> FLIP_FLOP_WALL_TORCH = registerNoItem("flip_flop_wall_torch", props -> new FlipFlopWallTorchBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().lightLevel(litBlockEmission(7)).sound(SoundType.WOOD)));
    public static final IRegistryObject<GlowstoneTorchBlock> GLOWSTONE_TORCH = registerNoItem("glowstone_torch", props -> new GlowstoneTorchBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().sound(SoundType.WOOD)));
    public static final IRegistryObject<GlowstoneWallTorchBlock> GLOWSTONE_WALL_TORCH = registerNoItem("glowstone_wall_torch", props -> new GlowstoneWallTorchBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().sound(SoundType.WOOD)));

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    private static ToIntFunction<BlockState> litBlockEmission(int litLevel) {
        return (state) -> {
            return state.getValue(BlockStateProperties.LIT) ? litLevel : 0;
        };
    }

    public static void init() {
    }
}
