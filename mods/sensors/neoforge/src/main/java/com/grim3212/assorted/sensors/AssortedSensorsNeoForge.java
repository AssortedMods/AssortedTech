package com.grim3212.assorted.sensors;

import com.grim3212.assorted.sensors.client.data.SensorsLanguageProvider;
import com.grim3212.assorted.sensors.client.data.SensorsManualProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeEntityTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import com.grim3212.assorted.sensors.client.data.SensorsBlockstateProvider;
import com.grim3212.assorted.sensors.client.data.SensorsItemModelProvider;
import com.grim3212.assorted.sensors.data.SensorsBlockLoot;
import com.grim3212.assorted.sensors.data.SensorsBlockTagProvider;
import com.grim3212.assorted.sensors.data.SensorsEntityTagProvider;
import com.grim3212.assorted.sensors.data.SensorsItemTagProvider;
import com.grim3212.assorted.sensors.data.SensorsRecipes;
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
public class AssortedSensorsNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedSensorsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        SensorsCommonMod.init();
    }

    /** One event for both halves; the data run's --server and --client flags pick which providers run. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(SensorsRecipes::new).blockLoot(SensorsBlockLoot::new).addTo(event);
        ForgeBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SensorsBlockTagProvider(packOutput, lookupProvider)));
        generator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, fileHelper, new SensorsItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        generator.addProvider(event.includeServer(), new ForgeEntityTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new SensorsEntityTagProvider(packOutput, lookupProvider)));

        generator.addProvider(event.includeClient(), new SensorsBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new SensorsItemModelProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new SensorsLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new SensorsManualProvider(packOutput));
    }
}
