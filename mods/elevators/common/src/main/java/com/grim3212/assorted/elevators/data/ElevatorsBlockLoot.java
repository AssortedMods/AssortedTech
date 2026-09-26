package com.grim3212.assorted.elevators.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;
import com.grim3212.assorted.elevators.common.block.InstantElevatorBlock;
import com.grim3212.assorted.elevators.common.block.ElevatorsBlocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElevatorsBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    // Loot sub providers are handed the registry lookup at construction now.
    public ElevatorsBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> ElevatorsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
        this.blocks.add(ElevatorsBlocks.ELEVATOR.get());
        this.blocks.add(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR.get());
        this.blocks.add(ElevatorsBlocks.ELEVATOR_LANDING.get());
        this.blocks.add(ElevatorsBlocks.CAMOUFLAGED_ELEVATOR_LANDING.get());
    }

    @Override
    public void generate() {
        for (Block b : this.blocks) {
            this.dropSelf(b);
        }
        this.dropKeepingColor(ElevatorsBlocks.INSTANT_ELEVATOR.get());
        this.dropKeepingColor(ElevatorsBlocks.CAMOUFLAGED_INSTANT_ELEVATOR.get());
    }

    /** The dropped item carries its colour and places back the same, so a dyed column survives being moved. */
    private void dropKeepingColor(Block block) {
        this.add(block, LootTable.lootTable().withPool(this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(block).apply(CopyBlockState.copyState(block).copy(InstantElevatorBlock.COLOR))))));
    }

}
