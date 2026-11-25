package com.grim3212.assorted.spikes;

import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import com.grim3212.assorted.spikes.client.data.SpikesBlockstateProvider;
import com.grim3212.assorted.spikes.client.data.SpikesLanguageProvider;
import com.grim3212.assorted.spikes.client.data.SpikesManualProvider;
import com.grim3212.assorted.spikes.data.SpikesBlockLoot;
import com.grim3212.assorted.spikes.data.SpikesBlockTagProvider;
import com.grim3212.assorted.spikes.data.SpikesItemTagProvider;
import com.grim3212.assorted.spikes.data.SpikesRecipes;
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
public class AssortedSpikesNeoForge {

    public AssortedSpikesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        SpikesCommonMod.init();
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(SpikesRecipes::new).blockLoot(SpikesBlockLoot::new).addTo(event);
        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SpikesBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new SpikesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));

        generator.addProvider(event.includeClient(), new SpikesBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new SpikesLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new SpikesManualProvider(packOutput));
    }
}
