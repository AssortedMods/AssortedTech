package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.FluidTags;

import java.util.concurrent.CompletableFuture;

public class BridgesRecipes extends ConditionalRecipeProvider {

    public BridgesRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_LASER.get(), 1).define('R', LibCommonTags.Items.SLIMEBALLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("IRI").pattern("III").unlockedBy("has_slime", has(LibCommonTags.Items.SLIMEBALLS)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_ACCEL.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', fluid(FluidTags.WATER)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_TRICK.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', LibCommonTags.Items.FEATHERS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_DEATH.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', fluid(FluidTags.LAVA)).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BridgesBlocks.BRIDGE_CONTROL_GRAVITY.get(), 1).define('L', BridgesBlocks.BRIDGE_CONTROL_LASER.get()).define('R', LibCommonTags.Items.ENDER_PEARLS).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("ILI").pattern("IRI").unlockedBy("has_laser_bridge", has(BridgesBlocks.BRIDGE_CONTROL_LASER.get())).save(this.output);
    }
}
