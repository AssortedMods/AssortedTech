package com.grim3212.assorted.grates.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Grates' gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class GratesTestSupport {

    private GratesTestSupport() {
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = GratesTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = GratesTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
