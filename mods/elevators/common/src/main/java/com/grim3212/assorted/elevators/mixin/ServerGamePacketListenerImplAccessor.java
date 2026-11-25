package com.grim3212.assorted.elevators.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The count of ticks the server has seen a player float for, which kicks them once it passes four
 * seconds where flight is not allowed. An elevator car is an entity, not a block, so a rider on it
 * floats as far as that check can tell; the car sets this back to nothing as it carries them.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessor {

    @Accessor("aboveGroundTickCount")
    void assortedelevators_setAboveGroundTickCount(int ticks);
}
