package com.grim3212.assorted.grates;

import com.grim3212.assorted.grates.client.data.GratesBlockstateProvider;
import com.grim3212.assorted.grates.client.data.GratesLanguageProvider;
import com.grim3212.assorted.grates.client.data.GratesManualProvider;
import com.grim3212.assorted.grates.data.GratesBlockLoot;
import com.grim3212.assorted.grates.data.GratesBlockTagProvider;
import com.grim3212.assorted.grates.data.GratesDataMapProvider;
import com.grim3212.assorted.grates.data.GratesItemTagProvider;
import com.grim3212.assorted.grates.data.GratesRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
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
public class AssortedGratesNeoForge {

    public AssortedGratesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        GratesCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new GratesRecipes.Runner(packOutput, lookupProvider));
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new GratesBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new GratesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(GratesBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
        event.addProvider(new GratesDataMapProvider(packOutput, lookupProvider));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new GratesBlockstateProvider(packOutput));
        event.addProvider(new GratesLanguageProvider(packOutput));
        event.addProvider(new GratesManualProvider(packOutput));
    }
}
