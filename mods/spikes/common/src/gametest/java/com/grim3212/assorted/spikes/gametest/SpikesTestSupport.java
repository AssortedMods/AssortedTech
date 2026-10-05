package com.grim3212.assorted.spikes.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.spikes.api.util.SpikeType;
import com.grim3212.assorted.spikes.common.block.SpikeBlock;
import com.grim3212.assorted.spikes.common.block.SpikesBlocks;
import net.minecraft.core.BlockPos;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Spikes' gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class SpikesTestSupport {

    private SpikesTestSupport() {
    }

    /** Two apart in both axes, so no pig can reach into a neighbouring spike. */
    static BlockPos spikeSlot(int index) {
        return new BlockPos((index % 5) * 2, 1, (index / 5) * 2);
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = SpikesTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = SpikesTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** The spike blocks are registered as one list, in {@link SpikeType} order. */
    static SpikeBlock spike(SpikeType type) {
        return SpikesBlocks.SPIKES.stream().map(IRegistryObject::get)
                .filter(block -> block.getSpikeType() == type).findFirst().orElseThrow();
    }
}
