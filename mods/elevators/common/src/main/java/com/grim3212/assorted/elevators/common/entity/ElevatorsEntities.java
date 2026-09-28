package com.grim3212.assorted.elevators.common.entity;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.elevators.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ElevatorsEntities {

    public static final RegistryProvider<EntityType<?>> ENTITIES = RegistryProvider.create(Registries.ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<EntityType<ElevatorCarEntity>> ELEVATOR_CAR = register("elevator_car", EntityType.Builder.<ElevatorCarEntity>of(ElevatorCarEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(20));

    private static <T extends Entity> IRegistryObject<EntityType<T>> register(final String name, final EntityType.Builder<T> builder) {
        final ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ENTITIES.register(name, () -> builder.build(key));
    }

    public static void init() {
    }
}
