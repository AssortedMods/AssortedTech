package com.grim3212.assorted.fan.client;

import com.grim3212.assorted.fan.client.particle.AirParticle;
import com.grim3212.assorted.fan.common.particle.FanParticleTypes;
import com.grim3212.assorted.fan.config.FanClientConfig;
import com.grim3212.assorted.lib.platform.ClientServices;

public class FanClient {

    public static final FanClientConfig CLIENT_CONFIG = new FanClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerParticle(FanParticleTypes.AIR, AirParticle.Factory::new);
    }

}
