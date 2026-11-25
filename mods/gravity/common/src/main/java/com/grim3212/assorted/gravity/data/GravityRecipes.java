package com.grim3212.assorted.gravity.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.gravity.Constants;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.item.GravityItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class GravityRecipes extends ConditionalRecipeProvider {

    public GravityRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GravityItems.GRAVITY_BOOTS.get(), 1).define('I', LibCommonTags.Items.INGOTS_IRON).define('A', GravityBlocks.ATTRACTOR.get()).pattern("I I").pattern("A A").unlockedBy("has_attractor", has(GravityBlocks.ATTRACTOR.get())).save(this.output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.ATTRACTOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern("RCR").pattern("IEI").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.GRAVITOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern(" C ").pattern("IEI").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.REPULSOR.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("RIR").pattern("ICI").pattern("RER").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.ATTRACTOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("IRI").pattern("RCR").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.GRAVITOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern(" R ").pattern("ICI").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, GravityBlocks.REPULSOR_DIRECTIONAL.get(), 1).define('C', Items.COMPASS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('E', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("RIR").pattern("ICI").pattern(" E ").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
    }
}
