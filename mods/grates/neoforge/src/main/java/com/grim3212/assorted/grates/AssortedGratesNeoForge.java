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
public class AssortedGratesNeoForge {

    public AssortedGratesNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        GratesCommonMod.init();
    }

    /** One event for both halves; the data run's --server and --client flags pick which providers run. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(GratesRecipes::new).blockLoot(GratesBlockLoot::new).addTo(event);
        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new GratesBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new GratesItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(event.includeServer(), new GratesDataMapProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new GratesBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new GratesLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new GratesManualProvider(packOutput));
    }
}
