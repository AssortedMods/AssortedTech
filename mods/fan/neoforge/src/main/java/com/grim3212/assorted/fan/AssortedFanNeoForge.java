package com.grim3212.assorted.fan;

import com.grim3212.assorted.fan.client.data.FanBlockstateProvider;
import com.grim3212.assorted.fan.client.data.FanLanguageProvider;
import com.grim3212.assorted.fan.client.data.FanManualProvider;
import com.grim3212.assorted.fan.data.FanBlockLoot;
import com.grim3212.assorted.fan.data.FanBlockTagProvider;
import com.grim3212.assorted.fan.data.FanRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
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
public class AssortedFanNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedFanNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        FanCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new FanRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new FanBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(FanBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new FanBlockstateProvider(packOutput));
        event.addProvider(new FanLanguageProvider(packOutput));
        event.addProvider(new FanManualProvider(packOutput));
    }
}
