package com.grim3212.assorted.sensors;

import com.grim3212.assorted.sensors.client.data.SensorsLanguageProvider;
import com.grim3212.assorted.sensors.client.data.SensorsManualProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.sensors.client.data.SensorsBlockstateProvider;
import com.grim3212.assorted.sensors.client.data.SensorsItemModelProvider;
import com.grim3212.assorted.sensors.data.SensorsBlockLoot;
import com.grim3212.assorted.sensors.data.SensorsBlockTagProvider;
import com.grim3212.assorted.sensors.data.SensorsEntityTagProvider;
import com.grim3212.assorted.sensors.data.SensorsItemTagProvider;
import com.grim3212.assorted.sensors.data.SensorsRecipes;
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
public class AssortedSensorsNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedSensorsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        SensorsCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new SensorsRecipes.Runner(packOutput, lookupProvider));
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new SensorsBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new SensorsItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new SensorsEntityTagProvider(packOutput, lookupProvider)));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(SensorsBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new SensorsBlockstateProvider(packOutput));
        event.addProvider(new SensorsItemModelProvider(packOutput));
        event.addProvider(new SensorsLanguageProvider(packOutput));
        event.addProvider(new SensorsManualProvider(packOutput));
    }
}
