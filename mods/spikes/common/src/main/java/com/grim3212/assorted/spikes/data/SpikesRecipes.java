package com.grim3212.assorted.spikes.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.Constants;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class SpikesRecipes extends ConditionalRecipeProvider {

    public SpikesRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Constants.MOD_ID);
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
            ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, b.get(), 6).define('X', mat).pattern("X X").pattern(" X ").pattern("XXX").unlockedBy("has_material", has(mat)).save(this.output, b.getId());
        }
    }

}
