package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.crafting.Recipe;

public class BridgesRecipes extends ConditionalRecipeProvider {

    public BridgesRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
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
}
