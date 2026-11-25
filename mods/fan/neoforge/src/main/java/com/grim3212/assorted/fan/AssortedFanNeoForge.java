package com.grim3212.assorted.fan;

import com.grim3212.assorted.fan.client.data.FanBlockstateProvider;
import com.grim3212.assorted.fan.client.data.FanLanguageProvider;
import com.grim3212.assorted.fan.client.data.FanManualProvider;
import com.grim3212.assorted.fan.data.FanBlockLoot;
import com.grim3212.assorted.fan.data.FanBlockTagProvider;
import com.grim3212.assorted.fan.data.FanRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
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
public class AssortedFanNeoForge {

    public AssortedFanNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        FanCommonMod.init();
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(FanRecipes::new).blockLoot(FanBlockLoot::new).addTo(event);
        generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new FanBlockTagProvider(packOutput, lookupProvider)));

        generator.addProvider(event.includeClient(), new FanBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new FanLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new FanManualProvider(packOutput));
    }
}
