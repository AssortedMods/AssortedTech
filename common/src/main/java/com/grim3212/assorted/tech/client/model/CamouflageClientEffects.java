package com.grim3212.assorted.tech.client.model;

import com.grim3212.assorted.lib.core.block.effects.ClientEffectUtils;
import com.grim3212.assorted.lib.core.block.effects.IBlockClientEffects;
import com.grim3212.assorted.tech.common.block.CamouflageBlock;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Hit and break particles of a disguised block come off its disguise.
 */
public class CamouflageClientEffects implements IBlockClientEffects {
    @Override
    public boolean addHitEffects(BlockState state, Level level, BlockPos pos, Direction dir, ParticleEngine manager) {
        if (state.getBlock() instanceof CamouflageBlock) {
            BlockState disguise = CamouflageBlock.getDisguise(level, pos);
            if (!disguise.isAir()) {
                return ClientEffectUtils.addHitEffects(level, pos, dir, disguise, manager);
            }
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (state.getBlock() instanceof CamouflageBlock) {
            BlockState disguise = CamouflageBlock.getDisguise(level, pos);
            if (!disguise.isAir()) {
                ClientEffectUtils.addBlockDestroyEffects(level, pos, disguise, manager, level);
                return true;
            }
        }
        return false;
    }
}
