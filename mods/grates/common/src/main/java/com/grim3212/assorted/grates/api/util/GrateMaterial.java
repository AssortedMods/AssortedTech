package com.grim3212.assorted.grates.api.util;

import com.grim3212.assorted.grates.api.GratesTags;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

/** Every metal an item grate comes in but copper, whose eight weathering grates are their own collection. */
public enum GrateMaterial implements StringRepresentable {
    // Vanilla materials
    IRON("iron", () -> LibCommonTags.Items.INGOTS_IRON, MapColor.METAL),
    GOLD("gold", () -> LibCommonTags.Items.INGOTS_GOLD, MapColor.GOLD),

    // Assorted Core added materials
    TIN("tin", () -> GratesTags.Items.INGOTS_TIN, MapColor.METAL),
    SILVER("silver", () -> GratesTags.Items.INGOTS_SILVER, MapColor.METAL),
    ALUMINUM("aluminum", () -> GratesTags.Items.INGOTS_ALUMINUM, MapColor.METAL),
    NICKEL("nickel", () -> GratesTags.Items.INGOTS_NICKEL, MapColor.METAL),
    PLATINUM("platinum", () -> GratesTags.Items.INGOTS_PLATINUM, MapColor.METAL),
    LEAD("lead", () -> GratesTags.Items.INGOTS_LEAD, MapColor.METAL),
    BRONZE("bronze", () -> GratesTags.Items.INGOTS_BRONZE, MapColor.METAL),
    ELECTRUM("electrum", () -> GratesTags.Items.INGOTS_ELECTRUM, MapColor.METAL),
    INVAR("invar", () -> GratesTags.Items.INGOTS_INVAR, MapColor.METAL),
    STEEL("steel", () -> GratesTags.Items.INGOTS_STEEL, MapColor.METAL);

    private final String name;
    private final Supplier<TagKey<Item>> material;
    private final MapColor mapColor;

    GrateMaterial(String name, Supplier<TagKey<Item>> material, MapColor mapColor) {
        this.name = name;
        this.material = material;
        this.mapColor = mapColor;
    }

    public TagKey<Item> getMaterial() {
        return material.get();
    }

    public MapColor getMapColor() {
        return mapColor;
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
