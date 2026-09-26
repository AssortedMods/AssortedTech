package com.grim3212.assorted.tech.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Jump on it to be taken to the next instant elevator of the same colour above, sneak to go to the
 * next one below. Dyed in place with any dye. {@link com.grim3212.assorted.tech.common.handlers.ElevatorInputHandler} does the moving.
 */
public class InstantElevatorBlock extends Block {

    public static final MapCodec<InstantElevatorBlock> CODEC = simpleCodec(InstantElevatorBlock::new);
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    public static final DyeColor DEFAULT_COLOR = DyeColor.PURPLE;

    public InstantElevatorBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DEFAULT_COLOR));
    }

    @Override
    protected MapCodec<? extends InstantElevatorBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        DyeColor dye = stack.get(DataComponents.DYE);
        if (dye == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (state.getValue(COLOR) == dye) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(COLOR, dye), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }
}
