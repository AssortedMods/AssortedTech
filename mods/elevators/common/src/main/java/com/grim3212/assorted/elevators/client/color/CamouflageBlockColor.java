package com.grim3212.assorted.elevators.client.color;

import com.grim3212.assorted.elevators.common.block.CamouflageBlock;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A disguise tints as its block does in that spot, so grass or leaves take the biome's colour.
 * Colours are ARGB, so an untinted quad is opaque white, -1.
 */
public class CamouflageBlockColor implements BlockColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level != null && pos != null && state.getBlock() instanceof CamouflageBlock) {
            BlockState disguise = CamouflageBlock.getDisguise(level, pos);
            if (!disguise.isAir()) {
                return ClientServices.CLIENT.getBlockColors().getColor(disguise, level, pos, tintIndex);
            }
        }
        return -1;
    }
}
