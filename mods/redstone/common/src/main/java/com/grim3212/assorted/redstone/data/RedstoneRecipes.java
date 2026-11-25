package com.grim3212.assorted.redstone.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public class RedstoneRecipes extends ConditionalRecipeProvider {

    public RedstoneRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RedstoneItems.FLIP_FLOP_TORCH.get(), 1).define('X', LibCommonTags.Items.RODS_WOODEN).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('P', LibCommonTags.Items.DYES_BLUE).pattern("P").pattern("X").pattern("R").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).save(this.output, RedstoneItems.FLIP_FLOP_TORCH.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RedstoneItems.GLOWSTONE_TORCH.get(), 1).define('X', LibCommonTags.Items.RODS_WOODEN).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('P', LibCommonTags.Items.DUSTS_GLOWSTONE).pattern("P").pattern("X").pattern("R").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).save(this.output, RedstoneItems.GLOWSTONE_TORCH.getId());
    }

}
