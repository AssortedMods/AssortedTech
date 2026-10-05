package com.grim3212.assorted.redstone.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.redstone.common.block.GlowstoneTorchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Redstone's gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class RedstoneTestSupport {

    private RedstoneTestSupport() {
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = RedstoneTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = RedstoneTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets every position back to air. Registered through {@code runBeforeTestEnd} by anything that
     * lights a torch, so it also runs on failure: block light crosses test box walls and would skew
     * a neighbouring test.
     */
    static void extinguish(GameTestHelper helper, BlockPos... positions) {
        for (BlockPos pos : positions) {
            helper.setBlock(pos, Blocks.AIR);
        }
    }

    /** The light level the block itself claims for the state at {@code pos}. */
    static int lightEmission(GameTestHelper helper, BlockPos pos) {
        BlockPos absolute = helper.absolutePos(pos);
        BlockState state = helper.getLevel().getBlockState(absolute);
        return ((GlowstoneTorchBlock) state.getBlock()).getLightEmission(state, helper.getLevel(), absolute);
    }
}
