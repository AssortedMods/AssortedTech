package com.grim3212.assorted.spikes.api.util;

import net.minecraft.core.registries.BuiltInRegistries;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.spikes.api.SpikesTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum SpikeType implements StringRepresentable {
    // Vanilla materials
    WOOD("wood", () -> ItemTags.PLANKS, 2f),
    STONE("stone", () -> ItemTags.STONE_TOOL_MATERIALS, 5f),
    IRON("iron", () -> LibCommonTags.Items.INGOTS_IRON, 8f),
    COPPER("copper", () -> LibCommonTags.Items.INGOTS_COPPER, 6.5f),
    DIAMOND("diamond", () -> LibCommonTags.Items.GEMS_DIAMOND, 10f),
    GOLD("gold", () -> LibCommonTags.Items.INGOTS_GOLD, 6f),
    NETHERITE("netherite", () -> LibCommonTags.Items.INGOTS_NETHERITE, 14f),
    AMETHYST("amethyst", () -> LibCommonTags.Items.GEMS_AMETHYST, 9f),
    EMERALD("emerald", () -> LibCommonTags.Items.GEMS_EMERALD, 11f),

    // Assorted Core added materials
    TIN("tin", () -> SpikesTags.Items.INGOTS_TIN, 4f),
    SILVER("silver", () -> SpikesTags.Items.INGOTS_SILVER, 9f),
    ALUMINUM("aluminum", () -> SpikesTags.Items.INGOTS_ALUMINUM, 3f),
    NICKEL("nickel", () -> SpikesTags.Items.INGOTS_NICKEL, 7f),
    PLATINUM("platinum", () -> SpikesTags.Items.INGOTS_PLATINUM, 11f),
    LEAD("lead", () -> SpikesTags.Items.INGOTS_LEAD, 7f),
    BRONZE("bronze", () -> SpikesTags.Items.INGOTS_BRONZE, 8f),
    ELECTRUM("electrum", () -> SpikesTags.Items.INGOTS_ELECTRUM, 9f),
    INVAR("invar", () -> SpikesTags.Items.INGOTS_INVAR, 8.5f),
    STEEL("steel", () -> SpikesTags.Items.INGOTS_STEEL, 10f),
    RUBY("ruby", () -> SpikesTags.Items.GEMS_RUBY, 8f),
    SAPPHIRE("sapphire", () -> SpikesTags.Items.GEMS_SAPPHIRE, 7.5f),
    TOPAZ("topaz", () -> SpikesTags.Items.GEMS_TOPAZ, 6.5f),
    PERIDOT("peridot", () -> SpikesTags.Items.GEMS_PERIDOT, 7f);

    private final String name;
    private final Supplier<TagKey<Item>> material;
    private final float damage;

    private SpikeType(String name, Supplier<TagKey<Item>> material, float damage) {
        this.name = name;
        this.material = material;
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    /**
     * Whether nothing can be this spike's material: its tag is empty, or no pack defines it at all,
     * as with an Assorted Core metal when Assorted Core is not installed.
     */
    public boolean isUncraftable() {
        return BuiltInRegistries.ITEM.get(this.getMaterial()).map(holders -> holders.size() < 1).orElse(true);
    }

    public TagKey<Item> getMaterial() {
        return material.get();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
