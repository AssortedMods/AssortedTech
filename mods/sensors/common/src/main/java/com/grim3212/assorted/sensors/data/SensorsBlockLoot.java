package com.grim3212.assorted.sensors.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SensorsBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    public SensorsBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> SensorsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
        this.blocks.add(SensorsBlocks.GPS_SENSOR.get());
        this.blocks.add(SensorsBlocks.UPGRADED_GPS_SENSOR.get());

        SensorsBlocks.SENSORS.forEach((sensor) -> this.blocks.add(sensor.get()));
    }

    @Override
    public void generate() {
        for (Block b : this.blocks) {
            this.dropSelf(b);
        }
    }

}
