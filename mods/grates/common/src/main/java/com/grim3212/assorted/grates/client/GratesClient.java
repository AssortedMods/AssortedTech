package com.grim3212.assorted.grates.client;

import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class GratesClient {

    public static void init() {
        // The layer the block models name, for Fabric, which ignores "render_type": every grate's holes are cut out.
        ClientServices.CLIENT.registerBlockRenderType(RenderType.cutout(), () -> GratesBlocks.allItemGrates().stream().map(IRegistryObject::get).map(Block.class::cast).toList());
    }
}
