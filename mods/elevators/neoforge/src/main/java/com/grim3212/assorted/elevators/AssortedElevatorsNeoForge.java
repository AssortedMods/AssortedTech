package com.grim3212.assorted.elevators;

import com.grim3212.assorted.elevators.client.data.ElevatorsBlockstateProvider;
import com.grim3212.assorted.elevators.client.data.ElevatorsLanguageProvider;
import com.grim3212.assorted.elevators.client.data.ElevatorsManualProvider;
import com.grim3212.assorted.elevators.common.handlers.ElevatorInputHandler;
import com.grim3212.assorted.elevators.data.ElevatorsBlockLoot;
import com.grim3212.assorted.elevators.data.ElevatorsBlockTagProvider;
import com.grim3212.assorted.elevators.data.ElevatorsRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedElevatorsNeoForge {

    public AssortedElevatorsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        ElevatorsCommonMod.init();

        // A level tick like Fabric's, rather than a player tick, so both loaders run it at the same point.
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                ElevatorInputHandler.tick(level);
            }
        });
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(ElevatorsRecipes::new).blockLoot(ElevatorsBlockLoot::new).addTo(event);
        generator.addProvider(event.includeServer(), new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, fileHelper, new ElevatorsBlockTagProvider(packOutput, lookupProvider)));

        generator.addProvider(event.includeClient(), new ElevatorsBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ElevatorsLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new ElevatorsManualProvider(packOutput));
    }
}
