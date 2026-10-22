package com.grim3212.assorted.redstone.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.common.item.RedstoneItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public class RedstoneRecipes extends ConditionalRecipeProvider {

    public RedstoneRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, RedstoneItems.FLIP_FLOP_TORCH.get(), 1).define('X', LibCommonTags.Items.RODS_WOODEN).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('P', LibCommonTags.Items.DYES_BLUE).pattern("P").pattern("X").pattern("R").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).save(this.output, recipeKey(RedstoneItems.FLIP_FLOP_TORCH.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, RedstoneItems.GLOWSTONE_TORCH.get(), 1).define('X', LibCommonTags.Items.RODS_WOODEN).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('P', LibCommonTags.Items.DUSTS_GLOWSTONE).pattern("P").pattern("X").pattern("R").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).save(this.output, recipeKey(RedstoneItems.GLOWSTONE_TORCH.getId()));
    }

    /** Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier} since 1.21.2. */
    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }
}
