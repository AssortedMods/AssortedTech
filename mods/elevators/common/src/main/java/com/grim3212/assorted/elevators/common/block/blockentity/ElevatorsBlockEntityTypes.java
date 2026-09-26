package com.grim3212.assorted.elevators.common.block.blockentity;

import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.Family;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ElevatorsBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<CamouflageBlockEntity>> CAMOUFLAGE = BLOCK_ENTITIES.register("camouflage", () -> Services.PLATFORM.createBlockEntityType(CamouflageBlockEntity::new, ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get(), ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()));

    public static void init() {
    }
}
