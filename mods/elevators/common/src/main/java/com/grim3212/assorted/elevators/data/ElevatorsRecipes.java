package com.grim3212.assorted.elevators.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ElevatorsRecipes extends ConditionalRecipeProvider {

    public ElevatorsRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // None: installing the mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.ELEVATOR.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('P', Items.PISTON).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("CCC").pattern("CPC").pattern("CRC").unlockedBy("has_copper", has(LibCommonTags.Items.INGOTS_COPPER)).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.ELEVATOR_LANDING.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("C").pattern("R").pattern("C").unlockedBy("has_elevator", has(ElevatorsBlocks.ELEVATOR.get())).save(this.output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.INSTANT_ELEVATOR.get(), 1).define('C', LibCommonTags.Items.INGOTS_COPPER).define('E', LibCommonTags.Items.ENDER_PEARLS).define('R', LibCommonTags.Items.DUSTS_REDSTONE).pattern("CRC").pattern("CEC").pattern("CCC").unlockedBy("has_ender_pearl", has(LibCommonTags.Items.ENDER_PEARLS)).save(this.output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get()).requires(ElevatorsBlocks.ELEVATOR.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_elevator", has(ElevatorsBlocks.ELEVATOR.get())).save(this.output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get()).requires(ElevatorsBlocks.ELEVATOR_LANDING.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_elevator_landing", has(ElevatorsBlocks.ELEVATOR_LANDING.get())).save(this.output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get()).requires(ElevatorsBlocks.INSTANT_ELEVATOR.get()).requires(Items.AMETHYST_SHARD).requires(LibCommonTags.Items.DYES).unlockedBy("has_instant_elevator", has(ElevatorsBlocks.INSTANT_ELEVATOR.get())).save(this.output);
    }
}
