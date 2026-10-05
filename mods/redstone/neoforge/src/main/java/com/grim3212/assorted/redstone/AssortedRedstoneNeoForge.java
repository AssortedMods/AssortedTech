package com.grim3212.assorted.redstone;

import com.grim3212.assorted.redstone.client.data.RedstoneBlockstateProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneItemModelProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneLanguageProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneManualProvider;
import com.grim3212.assorted.redstone.data.RedstoneBlockLoot;
import com.grim3212.assorted.redstone.data.RedstoneRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedRedstoneNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedRedstoneNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        RedstoneCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new RedstoneRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(RedstoneBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new RedstoneBlockstateProvider(packOutput));
        event.addProvider(new RedstoneItemModelProvider(packOutput));
        event.addProvider(new RedstoneLanguageProvider(packOutput));
        event.addProvider(new RedstoneManualProvider(packOutput));
    }
}
