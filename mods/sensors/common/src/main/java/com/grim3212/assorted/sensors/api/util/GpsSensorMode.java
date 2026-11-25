package com.grim3212.assorted.sensors.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** What a GPS sensor looks for at the position it watches. */
public enum GpsSensorMode implements StringRepresentable {
    PLAYERS("players"),
    MOBS("mobs"),
    ITEMS("items");

    public static final Codec<GpsSensorMode> CODEC = StringRepresentable.fromEnum(GpsSensorMode::values);

    private final String name;

    GpsSensorMode(String name) {
        this.name = name;
    }

    public GpsSensorMode next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
