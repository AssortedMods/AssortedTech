package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class BridgesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public BridgesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
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

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_LASER.get(), 1).define('R', LibCommonTags.Items.SLIMEBALLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("IRI").pattern("III").unlockedBy("has_slime", has(LibCommonTags.Items.SLIMEBALLS)).save(this.output, recipeKey(BridgesBlocks.BRIDGE_CONTROL_LASER.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_ACCEL.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', fluid(FluidTags.WATER)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output, recipeKey(BridgesBlocks.BRIDGE_CONTROL_ACCEL.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_TRICK.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', LibCommonTags.Items.FEATHERS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output, recipeKey(BridgesBlocks.BRIDGE_CONTROL_TRICK.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_DEATH.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', fluid(FluidTags.LAVA)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output, recipeKey(BridgesBlocks.BRIDGE_CONTROL_DEATH.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output, recipeKey(BridgesBlocks.BRIDGE_CONTROL_GRAVITY.getId()));
    }

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
            return new BridgesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
