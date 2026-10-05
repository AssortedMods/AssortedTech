package com.grim3212.assorted.gravity.client;

import com.grim3212.assorted.gravity.client.blockentity.GravityBlockEntityRenderer;
import com.grim3212.assorted.gravity.client.blockentity.GravityDirectionalBlockEntityRenderer;
import com.grim3212.assorted.gravity.common.block.blockentity.GravityBlockEntityTypes;
import com.grim3212.assorted.lib.platform.ClientServices;

public class GravityClient {

    public static void init() {
        ClientServices.CLIENT.registerBlockEntityRenderer(GravityBlockEntityTypes.GRAVITY, GravityBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(GravityBlockEntityTypes.GRAVITY_DIRECTIONAL, GravityDirectionalBlockEntityRenderer::new);
    }

}
