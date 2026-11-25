package com.grim3212.assorted.grates.data;

import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.block.ItemGrateBlock;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.grates.common.block.CopperFamily;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GratesRecipes extends ConditionalRecipeProvider {

    /**
     * Vanilla's copper grate at each stage, waxed and unwaxed. 1.21.1 has no copper bars, so a copper
     * item grate is woven from four of vanilla's grates of the same stage instead.
     */
    private static final CopperFamily<Item> VANILLA_COPPER_GRATES = CopperFamily.create("copper_grate",
            (age, name) -> vanillaCopperGrate(age, false), (age, name) -> vanillaCopperGrate(age, true));

    public GratesRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
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

        // Four bars woven into a panel, the metal mesh's recipe, and the same again from every stage of vanilla's copper grate.
        IRegistryObject<ItemGrateBlock> iron = GratesBlocks.ITEM_GRATES.get(GrateMaterial.IRON);
        fromBars(iron, Items.IRON_BARS, "has_iron_bars");
        List<IRegistryObject<ItemGrateBlock>> copperGrates = GratesBlocks.COPPER_ITEM_GRATES.asList();
        List<Item> vanillaGrates = VANILLA_COPPER_GRATES.asList();
        for (int i = 0; i < copperGrates.size(); i++) {
            fromBars(copperGrates.get(i), vanillaGrates.get(i), "has_copper_grate");
        }

        GratesBlocks.COPPER_ITEM_GRATES.zipUnwaxedWaxed((unwaxed, waxed) ->
                ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, waxed.get()).requires(unwaxed.get()).requires(Items.HONEYCOMB)
                        .group("waxed_copper_item_grate").unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
                        .save(this.output, waxed.getId().withSuffix("_from_honeycomb")));

        // Metals with no bars of their own are laid crosswise from ingots.
        GratesBlocks.ITEM_GRATES.forEach((material, grate) -> {
            if (material == GrateMaterial.IRON) {
                return;
            }

            TagKey<Item> ingot = material.getMaterial();
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, grate.get(), 8).define('I', ingot).pattern("I I").pattern(" I ").pattern("I I")
                    .unlockedBy("has_material", has(ingot)).save(this.output, grate.getId());
        });
    }

    private void fromBars(IRegistryObject<ItemGrateBlock> grate, Item bars, String criterion) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, grate.get(), 4).define('B', bars).pattern("BB").pattern("BB")
                .unlockedBy(criterion, has(bars)).save(this.output, grate.getId());
    }

    private static Item vanillaCopperGrate(WeatherState age, boolean waxed) {
        return switch (age) {
            case UNAFFECTED -> waxed ? Items.WAXED_COPPER_GRATE : Items.COPPER_GRATE;
            case EXPOSED -> waxed ? Items.WAXED_EXPOSED_COPPER_GRATE : Items.EXPOSED_COPPER_GRATE;
            case WEATHERED -> waxed ? Items.WAXED_WEATHERED_COPPER_GRATE : Items.WEATHERED_COPPER_GRATE;
            case OXIDIZED -> waxed ? Items.WAXED_OXIDIZED_COPPER_GRATE : Items.OXIDIZED_COPPER_GRATE;
        };
    }
}
