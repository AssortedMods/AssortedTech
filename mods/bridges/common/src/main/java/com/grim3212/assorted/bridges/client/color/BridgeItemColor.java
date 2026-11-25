package com.grim3212.assorted.bridges.client.color;

import com.grim3212.assorted.bridges.client.model.BridgeItemModel;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tints a held bridge as the block state it carries would be tinted, by asking that block's
 * {@code BlockColor} with no level - the colour a grass or leaf block item shows. Colours are ARGB,
 * so an untinted quad is opaque white, -1.
 */
public class BridgeItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        BlockState stored = BridgeItemModel.storedState(stack);
        if (stored.isAir()) {
            return -1;
        }

        return ClientServices.CLIENT.getBlockColors().getColor(stored, null, null, tintIndex);
    }
}
