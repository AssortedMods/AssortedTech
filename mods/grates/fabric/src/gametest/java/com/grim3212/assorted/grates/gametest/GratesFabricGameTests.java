package com.grim3212.assorted.grates.gametest;

import com.grim3212.assorted.grates.Constants;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Registers this mod's test functions on Fabric, from a dev-only second mod so the tests never ship.
 * Not Fabric's {@code @GameTest}, which would duplicate the shared {@code test_instance} jsons.
 */
public class GratesFabricGameTests implements ModInitializer {

    @Override
    public void onInitialize() {
        GratesGameTests.forEach((name, function) ->
                Registry.register(BuiltInRegistries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name), function));
    }
}
