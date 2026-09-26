package com.grim3212.assorted.gravity.common.block.blockentity;

import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.Family;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GravityBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<GravityBlockEntity>> GRAVITY = BLOCK_ENTITIES.register("gravity", () -> Services.PLATFORM.createBlockEntityType(GravityBlockEntity::new, GravityBlocks.ATTRACTOR.get(), GravityBlocks.REPULSOR.get(), GravityBlocks.GRAVITOR.get()));
    public static final IRegistryObject<BlockEntityType<GravityDirectionalBlockEntity>> GRAVITY_DIRECTIONAL = BLOCK_ENTITIES.register("gravity_directional", () -> Services.PLATFORM.createBlockEntityType(GravityDirectionalBlockEntity::new, GravityBlocks.ATTRACTOR_DIRECTIONAL.get(), GravityBlocks.REPULSOR_DIRECTIONAL.get(), GravityBlocks.GRAVITOR_DIRECTIONAL.get()));

    public static void init() {
    }
}
