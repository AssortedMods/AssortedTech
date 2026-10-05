package com.grim3212.assorted.gravity.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.gravity.common.block.GravityBlock;
import com.grim3212.assorted.gravity.common.block.GravityDirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.state.BlockState;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helpers shared by Assorted Gravity's gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class GravityTestSupport {

    private GravityTestSupport() {
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = GravityTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = GravityTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads POWERED off whichever gravity block is there. The plain and directional blocks each
     * declare their own "powered" property, and properties resolve by identity, so the wrong one
     * throws.
     */
    static boolean isPowered(GameTestHelper helper, BlockPos pos) {
        BlockState state = helper.getBlockState(pos);
        return state.getBlock() instanceof GravityDirectionalBlock
                ? state.getValue(GravityDirectionalBlock.POWERED)
                : state.getValue(GravityBlock.POWERED);
    }
}
