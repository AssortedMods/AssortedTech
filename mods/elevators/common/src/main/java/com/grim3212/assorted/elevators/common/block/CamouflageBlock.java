package com.grim3212.assorted.elevators.common.block;

import com.grim3212.assorted.lib.core.block.IBlockLightEmission;
import com.grim3212.assorted.lib.core.block.effects.IBlockClientEffects;
import com.grim3212.assorted.lib.core.block.effects.IBlockEffectSupplier;
import com.grim3212.assorted.lib.core.block.effects.IBlockLandingEffects;
import com.grim3212.assorted.lib.core.block.effects.IBlockRunningEffects;
import com.grim3212.assorted.lib.core.block.effects.ServerEffectUtils;
import com.grim3212.assorted.elevators.client.model.CamouflageClientEffects;
import com.grim3212.assorted.elevators.common.block.blockentity.CamouflageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A block that can wear another block's look: use a block on it to disguise it, sneak and use it
 * with an empty hand to take the disguise off. The disguise lives in a {@link CamouflageBlockEntity}.
 */
public interface CamouflageBlock extends EntityBlock, IBlockEffectSupplier, IBlockLandingEffects, IBlockRunningEffects, IBlockLightEmission {

    /** Only solid, opaque, full blocks with a plain model, so the disguise draws and lights like the real thing. */
    static boolean canDisguiseAs(BlockState state) {
        Object block = state.getBlock();
        return !(block instanceof EntityBlock) && !(block instanceof InstantElevatorBlock) && !(block instanceof ElevatorBlock) && !(block instanceof ElevatorLandingBlock)
                && state.getRenderShape() == RenderShape.MODEL && state.isSolidRender() && state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    /** Safe from the light engine's own thread. */
    static BlockState getDisguise(BlockGetter level, BlockPos pos) {
        return IBlockLightEmission.blockEntityAt(level, pos) instanceof CamouflageBlockEntity camouflage ? camouflage.getDisguise() : Blocks.AIR.defaultBlockState();
    }

    /** For {@code getLightEmission}, declared on each block as NeoForge's block extension has one too: a glowstone disguise glows. */
    static int lightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState disguise = getDisguise(level, pos);
        return disguise.isAir() ? state.getLightEmission() : disguise.getLightEmission();
    }

    /** Disguises the block as the one {@code stack} places; null if it cannot, so the block's own use goes on. */
    static @Nullable InteractionResult tryDisguise(ItemStack stack, Level level, BlockPos pos) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(level.getBlockEntity(pos) instanceof CamouflageBlockEntity camouflage)) {
            return null;
        }

        BlockState disguise = blockItem.getBlock().defaultBlockState();
        if (!canDisguiseAs(disguise)) {
            return null;
        }
        if (disguise == camouflage.getDisguise()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            camouflage.setDisguise(disguise);
            level.playSound(null, pos, disguise.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
        }
        return InteractionResult.SUCCESS;
    }

    /** Takes the disguise off for a sneaking player; null if there is none to take, so the block's own use goes on. */
    static @Nullable InteractionResult tryRemoveDisguise(Level level, BlockPos pos, Player player) {
        if (!player.isSecondaryUseActive() || !(level.getBlockEntity(pos) instanceof CamouflageBlockEntity camouflage) || camouflage.getDisguise().isAir()) {
            return null;
        }

        if (!level.isClientSide()) {
            camouflage.setDisguise(Blocks.AIR.defaultBlockState());
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CamouflageBlockEntity(pos, state);
    }

    @Override
    default Supplier<IBlockClientEffects> getClientEffects() {
        return CamouflageClientEffects::new;
    }

    /** For {@code addLandingEffects}, which each block has to declare itself as NeoForge's block extension has one too. */
    static boolean landingEffects(ServerLevel level, BlockPos pos, LivingEntity entity, int numberOfParticles) {
        BlockState disguise = getDisguise(level, pos);
        return !disguise.isAir() && ServerEffectUtils.addLandingEffects(disguise, level, entity, numberOfParticles);
    }

    /** For {@code addRunningEffects}, declared on each block for the same reason. */
    static boolean runningEffects(Level level, BlockPos pos, Entity entity) {
        BlockState disguise = getDisguise(level, pos);
        return !disguise.isAir() && ServerEffectUtils.addRunningEffects(disguise, level, entity);
    }
}
