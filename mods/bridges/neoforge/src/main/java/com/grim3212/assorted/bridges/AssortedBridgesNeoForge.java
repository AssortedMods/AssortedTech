package com.grim3212.assorted.bridges;

import com.grim3212.assorted.bridges.client.data.BridgesLanguageProvider;
import com.grim3212.assorted.bridges.client.data.BridgesManualProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.bridges.client.data.BridgesBlockstateProvider;
import com.grim3212.assorted.bridges.data.BridgesBlockLoot;
import com.grim3212.assorted.bridges.data.BridgesBlockTagProvider;
import com.grim3212.assorted.bridges.data.BridgesItemTagProvider;
import com.grim3212.assorted.bridges.data.BridgesRecipes;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedBridgesNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedBridgesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        BridgesCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(BridgesRecipes::new).blockLoot(BridgesBlockLoot::new).addTo(event);
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new BridgesBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new BridgesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
    }

    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        // BridgeModelProvider is gone. It only existed because Forge's ModelProvider was generic
        // over a builder type, so a second provider instance was the only way to get at a custom
        // loader builder. A custom loader is an ExtendedModelTemplateBuilder#customLoader call
        // inside whichever provider emits the model now, so the class had nothing left to do.
        event.addProvider(new BridgesBlockstateProvider(packOutput));
        event.addProvider(new BridgesLanguageProvider(packOutput));
        event.addProvider(new BridgesManualProvider(packOutput));
    }
}
