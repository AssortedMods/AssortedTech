package com.grim3212.assorted.bridges.client;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.bridges.client.color.BridgeItemColor;
import com.grim3212.assorted.bridges.client.model.BridgeUnbakedModel;
import com.grim3212.assorted.bridges.common.block.BridgeBlock;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.bridges.common.block.blockentity.BridgeBlockEntity;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BridgesClient {

    public static void init() {
        ClientServices.CLIENT.registerModelLoader(BridgeUnbakedModel.LOADER_NAME, BridgeUnbakedModel.Loader.INSTANCE);

        // A placed bridge is tinted as its stored block would be at the same position, and an empty
        // one in its type's colour. Colours are ARGB, so an opaque white is -1 rather than 0xFFFFFF.
        ClientServices.CLIENT.registerBlockColor((state, worldIn, pos, tintIndex) -> {
            if (worldIn != null && pos != null) {
                BlockEntity te = worldIn.getBlockEntity(pos);
                if (te instanceof BridgeBlockEntity bridge) {
                    BlockState stored = bridge.getStoredBlockState();
                    if (!stored.isAir()) {
                        return ClientServices.CLIENT.getBlockColors().getColor(stored, worldIn, pos, tintIndex);
                    }

                    return FastColor.ARGB32.opaque(state.getValue(BridgeBlock.TYPE).getRenderColor());
                }
            }
            return -1;
        }, () -> ImmutableList.of(BridgesBlocks.BRIDGE.get()));

        // The item form is drawn by the block model itself, resolved per stack through its item
        // overrides; only the tint of a stored grass or leaf block is left to an item colour.
        ClientServices.CLIENT.registerItemColor(new BridgeItemColor(), () -> ImmutableList.of(BridgesBlocks.BRIDGE.get()));
    }

}
