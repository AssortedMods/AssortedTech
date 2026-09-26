package com.grim3212.assorted.extruder.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.extruder.client.render.ExtruderModel;
import com.grim3212.assorted.extruder.client.render.ExtruderSpecialRenderer;
import com.grim3212.assorted.extruder.client.render.ExtruderRenderer;
import com.grim3212.assorted.extruder.client.screen.ExtruderScreen;
import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.inventory.ExtruderMenuTypes;

public class ExtruderClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(ExtruderModel.LAYER, ExtruderModel::createLayer);
        // The extruder items draw the entity's model; their model json picks this by id.
        ClientServices.CLIENT.registerSpecialModelRenderers(register -> register.registerSpecialModelRenderer(ExtruderSpecialRenderer.ID, ExtruderSpecialRenderer.Unbaked.MAP_CODEC));
        ClientServices.CLIENT.registerEntityRenderer(() -> ExtruderEntities.EXTRUDER.get(), ExtruderRenderer::new);
        ClientServices.CLIENT.registerScreen(ExtruderMenuTypes.EXTRUDER::get, ExtruderScreen::new);
    }

}
