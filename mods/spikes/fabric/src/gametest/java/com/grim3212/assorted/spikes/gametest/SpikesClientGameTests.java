package com.grim3212.assorted.spikes.gametest;

import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * What a headless server cannot see: a spike's tooltip as Fabric builds it. Fabric only, as NeoForge
 * has no client gametest; run with {@code ./gradlew :spikes:fabric:runClientGameTest}.
 */
public class SpikesClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            context.runOnClient(client -> {
                // Fabric only adds component tooltip lines on the client.
                ItemStack spike = new ItemStack(SpikesBlocks.SPIKES.get(0).get());
                List<String> spikeTooltip = spike.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL).stream()
                        .map(line -> line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString())
                        .toList();
                if (!spikeTooltip.contains("tooltip.spike.damage")) {
                    throw new AssertionError("a spike's tooltip is " + spikeTooltip);
                }
            });
        }
    }
}
