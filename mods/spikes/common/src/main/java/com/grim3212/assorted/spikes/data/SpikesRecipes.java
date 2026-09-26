package com.grim3212.assorted.spikes.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class SpikesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public SpikesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // A spike only loads when something fills its material's tag, which Assorted Core does for its metals.
        for (IRegistryObject<SpikeBlock> b : SpikesBlocks.SPIKES) {
            this.addConditions(itemTagExists(b.get().getSpikeType().getMaterial()), b.getId());
        }
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        for (IRegistryObject<SpikeBlock> b : SpikesBlocks.SPIKES) {
            TagKey<Item> mat = b.get().getSpikeType().getMaterial();
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, b.get(), 6).define('X', mat).pattern("X X").pattern(" X ").pattern("XXX").unlockedBy("has_material", has(mat)).save(this.output, recipeKey(b.getId()));
        }
    }

    /** Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier} since 1.21.2. */
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
            return new SpikesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
