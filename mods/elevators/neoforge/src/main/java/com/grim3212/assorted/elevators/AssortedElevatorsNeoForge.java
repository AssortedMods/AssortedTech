package com.grim3212.assorted.elevators;

import com.grim3212.assorted.elevators.client.data.ElevatorsLanguageProvider;
import com.grim3212.assorted.elevators.common.handlers.ElevatorInputHandler;
import com.grim3212.assorted.elevators.client.data.ElevatorsManualProvider;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.elevators.client.data.ElevatorsBlockstateProvider;
import com.grim3212.assorted.elevators.data.ElevatorsBlockLoot;
import com.grim3212.assorted.elevators.data.ElevatorsBlockTagProvider;
import com.grim3212.assorted.elevators.data.ElevatorsRecipes;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedElevatorsNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedElevatorsNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        ElevatorsCommonMod.init();

        // A level tick like Fabric's, rather than a player tick, so both loaders run it at the same point.
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                ElevatorInputHandler.tick(level);
            }
        });
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(ElevatorsRecipes::new).blockLoot(ElevatorsBlockLoot::new).addTo(event);
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new ElevatorsBlockTagProvider(packOutput, lookupProvider)));
    }

    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new ElevatorsBlockstateProvider(packOutput));
        event.addProvider(new ElevatorsLanguageProvider(packOutput));
        event.addProvider(new ElevatorsManualProvider(packOutput));
    }
}
