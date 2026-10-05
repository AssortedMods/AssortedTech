package com.grim3212.assorted.elevators.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * What a headless server cannot see: riding both elevators with real key presses. Fabric only, as
 * NeoForge has no client gametest; run with {@code ./gradlew :fabric:runClientGameTest}.
 */
public class ElevatorsClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            ElevatorClientTests.run(context, world);
        }
    }
}
