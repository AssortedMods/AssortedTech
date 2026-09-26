package com.grim3212.assorted.gravity.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class GravityRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public GravityRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, GravityItems.GRAVITY_BOOTS.get(), 1).define('I', LibCommonTags.Items.INGOTS_IRON).define('A', GravityBlocks.ATTRACTOR.get()).pattern("I I").pattern("A A").unlockedBy("has_attractor", has(GravityBlocks.ATTRACTOR.get())).save(this.output, recipeKey(GravityItems.GRAVITY_BOOTS.getId()));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.ATTRACTOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern("RCR").pattern("IEI").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.ATTRACTOR.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.GRAVITOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern(" C ").pattern("IEI").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.GRAVITOR.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.REPULSOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("RIR").pattern("ICI").pattern("RER").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.REPULSOR.getId()));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.ATTRACTOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern("RCR").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.ATTRACTOR_DIRECTIONAL.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.GRAVITOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern(" R ").pattern("ICI").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.GRAVITOR_DIRECTIONAL.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, GravityBlocks.REPULSOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("RIR").pattern("ICI").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(GravityBlocks.REPULSOR_DIRECTIONAL.getId()));
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier}
     * since 1.21.2 - the recipe manager keys them and a recipe no longer carries its own id. The
     * conditions map is still keyed by {@code Identifier}, so both forms are needed side by side.
     */
    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    /**
     * Recipe providers are not data providers any more - a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out. This is
     * what the loader datagen entry points register.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new GravityRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
