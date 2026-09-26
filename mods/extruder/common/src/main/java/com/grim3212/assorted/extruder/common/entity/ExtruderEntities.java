package com.grim3212.assorted.extruder.common.entity;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.Family;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ExtruderEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<EntityType<ExtruderEntity>> EXTRUDER = register("extruder", EntityType.Builder.<ExtruderEntity>of(ExtruderEntity::new, MobCategory.MISC).sized(0.875F, 0.875F).clientTrackingRange(10).updateInterval(1));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
