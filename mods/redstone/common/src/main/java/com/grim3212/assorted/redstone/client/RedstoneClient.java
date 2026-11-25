package com.grim3212.assorted.redstone.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public class RedstoneClient {

    public static void init() {
        // The layer the torch models name, for Fabric, which ignores "render_type".
        ClientServices.CLIENT.registerBlockRenderType(RenderType.cutout(), () -> List.of(RedstoneBlocks.FLIP_FLOP_TORCH.get(), RedstoneBlocks.FLIP_FLOP_WALL_TORCH.get(), RedstoneBlocks.GLOWSTONE_TORCH.get(), RedstoneBlocks.GLOWSTONE_WALL_TORCH.get()));
    }
}
