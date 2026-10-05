package com.grim3212.assorted.alarm;

import com.grim3212.assorted.alarm.client.data.AlarmBlockstateProvider;
import com.grim3212.assorted.alarm.client.data.AlarmLanguageProvider;
import com.grim3212.assorted.alarm.client.data.AlarmManualProvider;
import com.grim3212.assorted.alarm.data.AlarmBlockLoot;
import com.grim3212.assorted.alarm.data.AlarmBlockTagProvider;
import com.grim3212.assorted.alarm.data.AlarmRecipes;
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
public class AssortedAlarmNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedAlarmNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        AlarmCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new AlarmRecipes.Runner(packOutput, lookupProvider));
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new AlarmBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(AlarmBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
    }

    /** Client datagen: models, the language file and the manual, written into common for both loaders. */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new AlarmBlockstateProvider(packOutput));
        event.addProvider(new AlarmLanguageProvider(packOutput));
        event.addProvider(new AlarmManualProvider(packOutput));
    }
}
