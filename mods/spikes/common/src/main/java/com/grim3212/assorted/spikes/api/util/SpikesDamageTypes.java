package com.grim3212.assorted.spikes.api.util;

import com.grim3212.assorted.spikes.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SpikesDamageTypes {

    public static final ResourceKey<DamageType> SPIKE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "spike"));

    /**
     * A damage source for one of this mod's damage types, resolved against the level's damage type
     * registry ({@code DamageSources#source} is private).
     */
    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return source(level, key, null, null);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key), directEntity, causingEntity);
    }
}
