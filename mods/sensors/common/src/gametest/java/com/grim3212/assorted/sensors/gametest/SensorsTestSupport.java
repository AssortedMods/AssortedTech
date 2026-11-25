package com.grim3212.assorted.sensors.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.sensors.api.util.SensorType;
import com.grim3212.assorted.sensors.common.block.SensorBlock;
import com.grim3212.assorted.sensors.common.block.SensorsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.grim3212.assorted.lib.test.TestSupport.stand;
import static com.grim3212.assorted.lib.test.TestSupport.survivalPlayer;

/**
 * Helpers shared by Assorted Sensors' gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class SensorsTestSupport {

    private SensorsTestSupport() {
    }

    /** One subject per sensor type - the narrowest thing that satisfies that type's predicate. */
    static void spawnSensorSubject(GameTestHelper helper, SensorType type, BlockPos pos) {
        switch (type) {
            // Any entity at all, so a dropped item proves it is not quietly a living-entity check.
            case WOOD -> helper.spawnItem(Items.STICK, Vec3.atCenterOf(pos));
            case STONE -> helper.spawnWithNoFreeWill(EntityType.PIG, pos);
            case IRON -> stand(helper, survivalPlayer(helper), pos.below());
            case MOSSY_COBBLESTONE -> helper.spawnWithNoFreeWill(EntityType.ZOMBIE, pos);
            case PRISMARINE -> helper.spawnWithNoFreeWill(EntityType.SQUID, pos);
            case GOLD -> helper.spawnItem(Items.STICK, Vec3.atCenterOf(pos));
            case EMERALD -> helper.spawnWithNoFreeWill(EntityType.VILLAGER, pos);
            case NETHERRACK -> helper.spawnWithNoFreeWill(EntityType.PIGLIN, pos);
            case COBWEB -> helper.spawnWithNoFreeWill(EntityType.SILVERFISH, pos);
            case END_STONE -> helper.spawnWithNoFreeWill(EntityType.ENDERMITE, pos);
            case HAY_BALE -> helper.spawnWithNoFreeWill(EntityType.WOLF, pos);
            case FEATHER -> helper.spawnWithNoFreeWill(EntityType.CHICKEN, pos);
        }
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = SensorsTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = SensorsTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** The sensor blocks are registered as one list, in {@link SensorType} order. */
    static SensorBlock sensorOf(SensorType type) {
        return SensorsBlocks.SENSORS.stream().map(IRegistryObject::get)
                .filter(block -> block.getSensorType() == type).findFirst().orElseThrow();
    }
}
