package com.grim3212.assorted.alarm;

import com.grim3212.assorted.alarm.client.data.AlarmBlockstateProvider;
import com.grim3212.assorted.alarm.client.data.AlarmLanguageProvider;
import com.grim3212.assorted.alarm.client.data.AlarmManualProvider;
import com.grim3212.assorted.alarm.data.AlarmBlockLoot;
import com.grim3212.assorted.alarm.data.AlarmBlockTagProvider;
import com.grim3212.assorted.alarm.data.AlarmRecipes;
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
public class AssortedAlarmNeoForge {

    public AssortedAlarmNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        AlarmCommonMod.init();
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(AlarmRecipes::new).blockLoot(AlarmBlockLoot::new).addTo(event);
        generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new AlarmBlockTagProvider(packOutput, lookupProvider)));

        generator.addProvider(event.includeClient(), new AlarmBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new AlarmLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new AlarmManualProvider(packOutput));
    }
}
