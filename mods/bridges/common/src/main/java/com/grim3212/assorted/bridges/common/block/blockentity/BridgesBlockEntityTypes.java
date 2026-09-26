package com.grim3212.assorted.bridges.common.block.blockentity;

import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.Family;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BridgesBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<BridgeBlockEntity>> BRIDGE = BLOCK_ENTITIES.register("bridge", () -> Services.PLATFORM.createBlockEntityType(BridgeBlockEntity::new, BridgesBlocks.BRIDGE.get()));
    public static final IRegistryObject<BlockEntityType<BridgeControlBlockEntity>> BRIDGE_CONTROL = BLOCK_ENTITIES.register("bridge_control", () -> Services.PLATFORM.createBlockEntityType(BridgeControlBlockEntity::new, BridgesBlocks.BRIDGE_CONTROL_LASER.get(), BridgesBlocks.BRIDGE_CONTROL_ACCEL.get(), BridgesBlocks.BRIDGE_CONTROL_TRICK.get(), BridgesBlocks.BRIDGE_CONTROL_DEATH.get(), BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get()));

    public static void init() {
    }
}
