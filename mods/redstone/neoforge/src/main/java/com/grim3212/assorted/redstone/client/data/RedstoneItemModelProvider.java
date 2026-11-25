package com.grim3212.assorted.redstone.client.data;

import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * The two torch items, whose inventory model is a flat sprite from {@code block/}. That is why
 * {@code basicItem}, which takes the texture from the item id, is not used.
 */
public class RedstoneItemModelProvider extends ItemModelProvider {

    public RedstoneItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Assorted Redstone item models";
    }

    @Override
    protected void registerModels() {
        flatItem(RedstoneItems.FLIP_FLOP_TORCH.get(), "block/flip_flop_torch_off");
        flatItem(RedstoneItems.GLOWSTONE_TORCH.get(), "block/glowstone_torch_off");
    }

    private void flatItem(Item item, String texture) {
        this.withExistingParent(BuiltInRegistries.ITEM.getKey(item).getPath(), mcLoc("item/generated")).texture("layer0", modLoc(texture));
    }
}
