package com.grim3212.assorted.bridges.data;

import com.grim3212.assorted.bridges.api.BridgesTags;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BridgesItemTagProvider extends LibItemTagProvider {

    public BridgesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // Optional, so the tag still loads when Assorted Gravity is not installed.
        appender.apply(BridgesTags.Items.GRAVITY_BRIDGE_IMMUNE).addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("assortedgravity", "gravity_boots")));
    }
}
