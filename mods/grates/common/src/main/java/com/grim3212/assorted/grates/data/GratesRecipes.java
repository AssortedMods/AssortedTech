package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.block.ItemGrateBlock;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.WeatheringCopperCollection;

import java.util.concurrent.CompletableFuture;

public class GratesRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public GratesRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        // A grate from an ingot only loads when something fills that ingot's tag, which Assorted Core does for its metals.
        GratesBlocks.ITEM_GRATES.forEach((material, grate) -> {
            if (material != GrateMaterial.IRON && material != GrateMaterial.GOLD) {
                this.addConditions(itemTagExists(material.getMaterial()), grate.getId());
            }
        });
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // Four bars woven into a panel, the metal mesh's recipe, and the same again for every stage of copper bars.
        IRegistryObject<ItemGrateBlock> iron = GratesBlocks.ITEM_GRATES.get(GrateMaterial.IRON);
        fromBars(iron, Items.IRON_BARS, "has_iron_bars");
        WeatheringCopperCollection.zipApply(GratesBlocks.COPPER_ITEM_GRATES, Items.COPPER_BARS, (grate, bars) -> fromBars(grate, bars, "has_copper_bars"));

        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) ->
                ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, waxed.get()).requires(unwaxed.get()).requires(Items.HONEYCOMB)
                        .group("waxed_copper_item_grate").unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
                        .save(this.output, recipeKey(waxed.getId().withSuffix("_from_honeycomb"))));

        // Metals with no bars of their own are laid crosswise from ingots.
        GratesBlocks.ITEM_GRATES.forEach((material, grate) -> {
            if (material == GrateMaterial.IRON) {
                return;
            }

            TagKey<Item> ingot = material.getMaterial();
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, grate.get(), 8).define('I', ingot).pattern("I I").pattern(" I ").pattern("I I")
                    .unlockedBy("has_material", has(ingot)).save(this.output, recipeKey(grate.getId()));
        });
    }

    private void fromBars(IRegistryObject<ItemGrateBlock> grate, Item bars, String criterion) {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.BUILDING_BLOCKS, grate.get(), 4).define('B', bars).pattern("BB").pattern("BB")
                .unlockedBy(criterion, has(bars)).save(this.output, recipeKey(grate.getId()));
    }

    /** Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier} since 1.21.2. */
    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    /**
     * Recipe providers are not data providers any more; a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new GratesRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
