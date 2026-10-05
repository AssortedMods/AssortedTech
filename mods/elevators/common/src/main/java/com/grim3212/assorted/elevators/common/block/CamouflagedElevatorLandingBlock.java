package com.grim3212.assorted.elevators.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
 * An {@link ElevatorLandingBlock} that can be disguised as the wall it is built into.
 */
public class CamouflagedElevatorLandingBlock extends ElevatorLandingBlock implements CamouflageBlock {

    public static final MapCodec<CamouflagedElevatorLandingBlock> CODEC = simpleCodec(CamouflagedElevatorLandingBlock::new);

    public CamouflagedElevatorLandingBlock(Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends CamouflagedElevatorLandingBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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
