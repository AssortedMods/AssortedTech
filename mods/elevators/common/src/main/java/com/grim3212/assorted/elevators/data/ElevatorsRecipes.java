package com.grim3212.assorted.elevators.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class ElevatorsRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public ElevatorsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // None: installing the mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.ELEVATOR.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('P', Items.PISTON).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("CCC").pattern("CPC").pattern("CRC").unlockedBy("has_copper", has(LibCommonTags.Items.INGOTS_COPPER)).save(this.output, recipeKey(ElevatorsBlocks.ELEVATOR.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.ELEVATOR_LANDING.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("C").pattern("R").pattern("C").unlockedBy("has_elevator", has(ElevatorsBlocks.ELEVATOR.get())).save(this.output, recipeKey(ElevatorsBlocks.ELEVATOR_LANDING.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.INSTANT_ELEVATOR.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('E', LibCommonTags.Items.ENDER_PEARLS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("CRC").pattern("CEC").pattern("CCC").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output, recipeKey(ElevatorsBlocks.INSTANT_ELEVATOR.getId()));
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()).requires(ElevatorsBlocks.ELEVATOR.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_elevator", has(ElevatorsBlocks.ELEVATOR.get())).save(this.output, recipeKey(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.getId()));
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()).requires(ElevatorsBlocks.ELEVATOR_LANDING.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_elevator_landing", has(ElevatorsBlocks.ELEVATOR_LANDING.get())).save(this.output, recipeKey(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.getId()));
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get()).requires(ElevatorsBlocks.INSTANT_ELEVATOR.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_instant_elevator", has(ElevatorsBlocks.INSTANT_ELEVATOR.get())).save(this.output, recipeKey(ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.getId()));
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
            return new ElevatorsRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
