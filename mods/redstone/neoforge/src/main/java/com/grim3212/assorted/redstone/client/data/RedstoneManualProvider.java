package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.Family;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/**
 * This part's chapter of the Assorted Tech section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class RedstoneManualProvider extends LibManualProvider {

    public RedstoneManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        ChapterBuilder redstone = this.chapter("redstone", 5);
        redstone.recipes("glowstone_torch", RedstoneBlocks.GLOWSTONE_TORCH.get())
                .opens(RedstoneBlocks.GLOWSTONE_TORCH.get(), RedstoneBlocks.GLOWSTONE_WALL_TORCH.get());
        redstone.recipes("flip_flop_torch", RedstoneBlocks.FLIP_FLOP_TORCH.get())
                .opens(RedstoneBlocks.FLIP_FLOP_TORCH.get(), RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get());
    }
}
