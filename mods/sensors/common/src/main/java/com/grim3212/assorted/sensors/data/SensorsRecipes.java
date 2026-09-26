package com.grim3212.assorted.sensors.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.sensors.Constants;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import com.grim3212.assorted.sensors.common.item.SensorsItems;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class SensorsRecipes extends ConditionalRecipeProvider {

    private final HolderGetter<Item> items;

    public SensorsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
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

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, SensorsItems.GPS.get(), 1).define('I', LibCommonTags.Items.INGOTS_IRON).define('C', Items.COMPASS).define('M', Items.MAP).pattern("ICI").pattern(" M ").unlockedBy("has_compass", has(Items.COMPASS)).save(this.output, recipeKey(SensorsItems.GPS.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, SensorsBlocks.GPS_SENSOR.get(), 1).define('R', LibCommonTags.Items.STORAGE_BLOCKS_REDSTONE).define('L', Items.REDSTONE_LAMP).define('G', SensorsItems.GPS.get()).pattern(" R ").pattern("LGL").pattern(" R ").unlockedBy("has_gps", has(SensorsItems.GPS.get())).save(this.output, recipeKey(SensorsBlocks.GPS_SENSOR.getId()));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, SensorsBlocks.UPGRADED_GPS_SENSOR.get(), 1).define('G', LibCommonTags.Items.STORAGE_BLOCKS_GOLD).define('X', LibCommonTags.Items.INGOTS_GOLD).define('P', SensorsItems.GPS.get()).define('S', SensorsBlocks.GPS_SENSOR.get()).pattern("GXG").pattern("PSP").pattern("GXG").unlockedBy("has_gps_sensor", has(SensorsBlocks.GPS_SENSOR.get())).save(this.output, recipeKey(SensorsBlocks.UPGRADED_GPS_SENSOR.getId()));

        for (IRegistryObject<SensorBlock> b : SensorsBlocks.SENSORS) {
            Ingredient mat = b.get().getSensorType().getCraftingMaterial(this.items);
            ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, b.get(), 1).define('X', LibCommonTags.Items.INGOTS_IRON).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('G', LibCommonTags.Items.GLASS).define('M', mat).pattern("XGX").pattern("MRM").pattern("XMX").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(this.output, recipeKey(b.getId()));
        }
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier}
     * since 1.21.2 - the recipe manager keys them and a recipe no longer carries its own id.
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
            return new SensorsRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
