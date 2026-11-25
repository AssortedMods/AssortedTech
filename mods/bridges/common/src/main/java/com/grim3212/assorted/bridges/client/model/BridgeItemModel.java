package com.grim3212.assorted.bridges.client.model;

import com.grim3212.assorted.bridges.common.properties.BridgesModelProperties;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a bridge item is drawn from: the block its stack has stored under {@code stored_state},
 * handed to the bridge's block model as the model data its block entity would supply.
 * {@link BridgeBaseBakedModel} resolves every stack through these, by way of AssortedLib's
 * {@link com.grim3212.assorted.lib.client.model.item.DataAwareItemOverrides}, so an item and a placed
 * bridge holding the same block draw identically.
 */
public final class BridgeItemModel {

    private BridgeItemModel() {
    }

    public static BlockState storedState(ItemStack stack) {
        if (!NBTHelper.hasTag(stack, "stored_state")) {
            return Blocks.AIR.defaultBlockState();
        }

        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), NBTHelper.getTag(stack, "stored_state"));
    }

    public static IBlockModelData modelData(ItemStack stack) {
        return IModelDataBuilder.create().withInitial(BridgesModelProperties.BLOCK_STATE, storedState(stack)).build();
    }
}
