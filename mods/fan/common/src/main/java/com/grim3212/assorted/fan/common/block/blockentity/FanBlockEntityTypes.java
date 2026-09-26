package com.grim3212.assorted.fan.common.block.blockentity;

import com.grim3212.assorted.fan.Constants;
import com.grim3212.assorted.fan.Family;
import com.grim3212.assorted.fan.common.block.FanBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FanBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<FanBlockEntity>> FAN = BLOCK_ENTITIES.register("fan", () -> Services.PLATFORM.createBlockEntityType(FanBlockEntity::new, FanBlocks.FAN.get()));

    public static void init() {
    }
}
