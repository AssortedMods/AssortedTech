package com.grim3212.assorted.spikes.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class SpikesClient {

    public static void init() {
        // The layer the spike models name, for Fabric, which ignores "render_type".
        ClientServices.CLIENT.registerBlockRenderType(RenderType.cutout(), () -> SpikesBlocks.SPIKES.stream().<Block>map(Supplier::get).toList());
    }
}
