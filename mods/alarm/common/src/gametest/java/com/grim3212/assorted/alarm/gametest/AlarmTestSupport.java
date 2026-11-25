package com.grim3212.assorted.alarm.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.phys.Vec3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.grim3212.assorted.lib.test.TestSupport.whenTestEnds;

/**
 * Helpers shared by Assorted Alarm's gametest classes, which import them statically, alongside
 * AssortedLib's {@code TestSupport}.
 */
final class AlarmTestSupport {

    private AlarmTestSupport() {
    }

    /** Reads a json off the mod's own classpath, or null if it is not there. */
    static JsonObject readJson(String path) {
        try (InputStream in = AlarmTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = AlarmTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A joined player standing at {@code pos}, as {@code TestSupport.survivalPlayer} makes one, but
     * keeping hold of its channel: every packet the server sends the player waits there, so a test
     * can hear the sounds played near it. Whatever joining sent is already drained.
     */
    static EmbeddedChannel listenerAt(GameTestHelper helper, BlockPos pos) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "alarm-listener"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        EmbeddedChannel channel = new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        whenTestEnds(helper, () -> level.getServer().getPlayerList().remove(player));

        Vec3 at = helper.absoluteVec(Vec3.atBottomCenterOf(pos));
        player.setPos(at.x, at.y, at.z);
        soundsHeard(channel);
        return channel;
    }

    /** The sounds sent down {@code channel} since it was last read. */
    static List<ClientboundSoundPacket> soundsHeard(EmbeddedChannel channel) {
        List<ClientboundSoundPacket> sounds = new ArrayList<>();
        // The server writes during its tick and flushes only the connections it ticks, which this is not.
        channel.flushOutbound();
        for (Object sent = channel.readOutbound(); sent != null; sent = channel.readOutbound()) {
            if (sent instanceof ClientboundSoundPacket sound) {
                sounds.add(sound);
            }
        }
        return sounds;
    }
}
