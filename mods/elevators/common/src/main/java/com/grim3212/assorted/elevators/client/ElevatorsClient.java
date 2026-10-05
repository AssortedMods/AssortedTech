package com.grim3212.assorted.elevators.client;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.elevators.client.color.CamouflageTintSource;
import com.grim3212.assorted.elevators.client.model.CamouflageModelLoader;
import com.grim3212.assorted.elevators.client.model.CamouflageUnbakedModel;
import com.grim3212.assorted.elevators.client.render.ElevatorCarRenderer;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.elevators.common.entity.ElevatorsEntities;

public class ElevatorsClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityRenderer(() -> ElevatorsEntities.ELEVATOR_CAR.get(), ElevatorCarRenderer::new);

        ClientServices.CLIENT.registerModelLoader(CamouflageUnbakedModel.LOADER_NAME, CamouflageModelLoader.INSTANCE);
        ClientServices.CLIENT.registerBlockColor(new CamouflageTintSource(), () -> ImmutableList.of(ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()));
    }

}
