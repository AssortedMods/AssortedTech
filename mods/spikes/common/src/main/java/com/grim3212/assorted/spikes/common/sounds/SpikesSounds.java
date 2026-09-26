package com.grim3212.assorted.spikes.common.sounds;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.Family;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SpikesSounds {

    public static final RegistryProvider<SoundEvent> SOUNDS = RegistryProvider.create(Registries.SOUND_EVENT, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<SoundEvent> SPIKE_DEPLOY = registerSound("spike_deploy");
    public static final IRegistryObject<SoundEvent> SPIKE_CLOSE = registerSound("spike_close");

    private static IRegistryObject<SoundEvent> registerSound(String name) {
        Identifier loc = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(loc));
    }

    public static void init() {
    }
}
