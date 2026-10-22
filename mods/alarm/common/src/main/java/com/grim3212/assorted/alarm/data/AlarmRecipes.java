package com.grim3212.assorted.alarm.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.AlarmBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class AlarmRecipes extends ConditionalRecipeProvider {

    public AlarmRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // Nothing is conditioned: installing this mod is what turns its recipes on.
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.REDSTONE, AlarmBlocks.ALARM.get(), 1).define('X', Items.LEVER).define('R', LibCommonTags.Items.DUSTS_REDSTONE).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("III").pattern("IRI").pattern("IXI").unlockedBy("has_redstone", has(LibCommonTags.Items.DUSTS_REDSTONE)).save(this.output, recipeKey(AlarmBlocks.ALARM.getId()));
    }

    /** Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw {@code Identifier} since 1.21.2. */
    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }
}
