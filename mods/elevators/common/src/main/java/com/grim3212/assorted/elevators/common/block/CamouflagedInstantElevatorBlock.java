package com.grim3212.assorted.elevators.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * An {@link InstantElevatorBlock} that can be disguised as another block. Dyeing still sets its colour.
 */
public class CamouflagedInstantElevatorBlock extends InstantElevatorBlock implements CamouflageBlock {

    public static final MapCodec<CamouflagedInstantElevatorBlock> CODEC = simpleCodec(CamouflagedInstantElevatorBlock::new);

    public CamouflagedInstantElevatorBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends CamouflagedInstantElevatorBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.has(DataComponents.DYE)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        InteractionResult disguised = CamouflageBlock.tryDisguise(stack, level, pos);
        return disguised != null ? disguised : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InteractionResult removed = CamouflageBlock.tryRemoveDisguise(level, pos, player);
        return removed != null ? removed : InteractionResult.PASS;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return CamouflageBlock.landingEffects(level, pos, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return CamouflageBlock.runningEffects(level, pos, entity);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return CamouflageBlock.lightEmission(state, level, pos);
    }
}
