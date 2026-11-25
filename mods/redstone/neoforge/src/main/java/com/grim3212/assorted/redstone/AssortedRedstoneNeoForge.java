package com.grim3212.assorted.redstone;

import com.grim3212.assorted.lib.data.ForgeRegistryEntries;
import com.grim3212.assorted.redstone.client.data.RedstoneBlockstateProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneItemModelProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneLanguageProvider;
import com.grim3212.assorted.redstone.client.data.RedstoneManualProvider;
import com.grim3212.assorted.redstone.data.RedstoneBlockLoot;
import com.grim3212.assorted.redstone.data.RedstoneRecipes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Constants.MOD_ID)
public class AssortedRedstoneNeoForge {

    public AssortedRedstoneNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::gatherData);

        RedstoneCommonMod.init();
    }

    /** One event for both halves; each provider is added for the half its output belongs to. */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ForgeRegistryEntries.of(Constants.MOD_ID).recipes(RedstoneRecipes::new).blockLoot(RedstoneBlockLoot::new).addTo(event);

        generator.addProvider(event.includeClient(), new RedstoneBlockstateProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new RedstoneItemModelProvider(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new RedstoneLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new RedstoneManualProvider(packOutput));
    }
}
