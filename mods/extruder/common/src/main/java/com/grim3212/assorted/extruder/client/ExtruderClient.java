package com.grim3212.assorted.extruder.client;

import com.grim3212.assorted.extruder.client.render.ExtruderItemRenderer;
import com.grim3212.assorted.extruder.client.render.ExtruderModel;
import com.grim3212.assorted.extruder.client.render.ExtruderRenderer;
import com.grim3212.assorted.extruder.client.screen.ExtruderScreen;
import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.inventory.ExtruderMenuTypes;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.lib.platform.ClientServices;

public class ExtruderClient {

    public static void init() {
        ClientServices.CLIENT.registerEntityLayer(ExtruderModel.LAYER, ExtruderModel::createLayer);
        // The extruder items draw the entity's model; their model json is builtin/entity.
        ClientServices.CLIENT.registerSpecialModelRenderers(register -> ExtruderItems.EXTRUDERS.forEach((type, extruder) ->
                register.registerItemRenderer(extruder, () -> new ExtruderItemRenderer(type))));
        ClientServices.CLIENT.registerEntityRenderer(() -> ExtruderEntities.EXTRUDER.get(), ExtruderRenderer::new);
        ClientServices.CLIENT.registerScreen(ExtruderMenuTypes.EXTRUDER::get, ExtruderScreen::new);
    }

}
