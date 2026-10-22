package com.grim3212.assorted.extruder.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.api.ExtruderTags;
import com.grim3212.assorted.extruder.api.util.ExtruderType;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class ExtruderRecipes extends ConditionalRecipeProvider {

    public ExtruderRecipes(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        // An Assorted Tools material's extruder only once Assorted Tools gives it tools.
        for (ExtruderType type : ExtruderType.values()) {
            Identifier id = ExtruderItems.EXTRUDERS.get(type).getId();
            this.addConditions(and(itemTagExists(type.getPickaxes()), itemTagExists(type.getShovels()), itemTagExists(type.getAxes())), id, id.withSuffix(ALT));
        }
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        // The pickaxe on top and the axe and shovel either side of the middle, and an alt recipe with
        // those two swapped so recipe viewers show both. Level 0 has a piston in the middle and a
        // dispenser under it; every level above has any extruder of the level below instead.
        for (ExtruderType type : ExtruderType.values()) {
            Identifier id = ExtruderItems.EXTRUDERS.get(type).getId();
            this.extruder(type, "AMS", id);
            this.extruder(type, "SMA", id.withSuffix(ALT));
        }
    }

    private static final String ALT = "_alt";

    /** An extruder from its tools and, for {@code M}, a piston or an extruder of the level below. */
    private void extruder(ExtruderType type, String middleRow, Identifier id) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(this.items, RecipeCategory.TOOLS, ExtruderItems.extruder(type), 1).define('P', type.getPickaxes()).define('S', type.getShovels()).define('A', type.getAxes()).pattern(" P ").pattern(middleRow).unlockedBy("has_pickaxe", has(type.getPickaxes()));
        if (type.getLevel() == 0) {
            builder.define('M', Items.PISTON).define('D', Items.DISPENSER).pattern(" D ");
        } else {
            TagKey<Item> previous = ExtruderTags.Items.extruders(type.getLevel() - 1);
            builder.define('M', previous).unlockedBy("has_extruder", has(previous));
        }
        builder.save(this.output, recipeKey(id));
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} since 1.21.2, but the conditions map is
     * still keyed by {@code Identifier}, so both forms are needed side by side.
     */
    private static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }
}
