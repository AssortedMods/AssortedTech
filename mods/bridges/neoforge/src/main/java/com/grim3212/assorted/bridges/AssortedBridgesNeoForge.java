package com.grim3212.assorted.bridges;

import com.grim3212.assorted.bridges.client.data.BridgesBlockstateProvider;
import com.grim3212.assorted.bridges.client.data.BridgesLanguageProvider;
import com.grim3212.assorted.bridges.client.data.BridgesManualProvider;
import com.grim3212.assorted.bridges.data.BridgesBlockLoot;
import com.grim3212.assorted.bridges.data.BridgesBlockTagProvider;
import com.grim3212.assorted.bridges.data.BridgesItemTagProvider;
import com.grim3212.assorted.bridges.data.BridgesRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedBridgesNeoForge {

    public AssortedBridgesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        BridgesCommonMod.init();
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(BridgesRecipes::new).blockLoot(BridgesBlockLoot::new).addTo(event);
        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new BridgesBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new BridgesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));

        generator.addProvider(event.includeClient(), new BridgesBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new BridgesLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new BridgesManualProvider(packOutput));
    }
}
