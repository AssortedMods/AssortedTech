package com.grim3212.assorted.gravity;

import com.grim3212.assorted.gravity.client.data.GravityBlockstateProvider;
import com.grim3212.assorted.gravity.client.data.GravityEquipmentAssetProvider;
import com.grim3212.assorted.gravity.client.data.GravityItemModelProvider;
import com.grim3212.assorted.gravity.client.data.GravityLanguageProvider;
import com.grim3212.assorted.gravity.client.data.GravityManualProvider;
import com.grim3212.assorted.gravity.data.GravityBlockLoot;
import com.grim3212.assorted.gravity.data.GravityBlockTagProvider;
import com.grim3212.assorted.gravity.data.GravityRecipes;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedGravityNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedGravityNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);

        GravityCommonMod.init();
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(GravityRecipes::new).blockLoot(GravityBlockLoot::new).addTo(event);
        event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new GravityBlockTagProvider(packOutput, lookupProvider)));
    }

    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        event.addProvider(new GravityBlockstateProvider(packOutput));
        event.addProvider(new GravityItemModelProvider(packOutput));
        event.addProvider(new GravityEquipmentAssetProvider(packOutput));
        event.addProvider(new GravityLanguageProvider(packOutput));
        event.addProvider(new GravityManualProvider(packOutput));
    }
}
