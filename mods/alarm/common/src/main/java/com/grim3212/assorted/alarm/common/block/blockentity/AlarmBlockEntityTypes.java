package com.grim3212.assorted.alarm.common.block.blockentity;

import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.Family;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AlarmBlockEntityTypes {

    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<BlockEntityType<AlarmBlockEntity>> ALARM = BLOCK_ENTITIES.register("alarm", () -> Services.PLATFORM.createBlockEntityType(AlarmBlockEntity::new, AlarmBlocks.ALARM.get()));

    public static void init() {
    }
}
