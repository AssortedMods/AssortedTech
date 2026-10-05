package com.grim3212.assorted.elevators.client.color;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.elevators.common.block.CamouflageBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A disguise tints as its block does in that spot, so grass or leaves take the biome's colour.
 */
public class CamouflageTintSource implements BlockTintSource {

    @Override
    public int color(BlockState state) {
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        if (state.getBlock() instanceof CamouflageBlock) {
            BlockState disguise = CamouflageBlock.getDisguise(level, pos);
            BlockTintSource source = disguise.isAir() ? null : ClientServices.CLIENT.getBlockColors().getTintSource(disguise, 0);
            if (source != null) {
                return source.colorInWorld(disguise, level, pos);
            }
        }
        return -1;
    }
}
