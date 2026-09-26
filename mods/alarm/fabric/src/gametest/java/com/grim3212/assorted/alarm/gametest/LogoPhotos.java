package com.grim3212.assorted.alarm.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/** Temporary: takes the raw square shot for this mod's logo. Not a test. */
public class LogoPhotos implements FabricClientGameTest {

    // The part is read off this copy's package, so one file serves every mod.
    private static final String PART = LogoPhotos.class.getPackageName().split("\\.")[3];
    private static final Set<String> DESERT = Set.of("dragonfruit");
    private static final Set<String> FLAT = Set.of("structures");

    private static BlockPos origin = BlockPos.ZERO;

    @Override
    public void runTest(ClientGameTestContext context) {
        boolean desert = DESERT.contains(PART);
        context.getInput().resizeWindow(1024, 1024);
        context.runOnClient(client -> {
            client.options.guiScale().set(2);
            client.options.fov().set(60);
        });

        ResourceKey<Biome> biomeKey = desert ? Biomes.DESERT : Biomes.PLAINS;
        try (TestSingleplayerContext world = context.worldBuilder().adjustSettings(creator -> creator.updateDimensions((registries, dimensions) -> {
            Holder<Biome> biome = registries.lookupOrThrow(Registries.BIOME).getOrThrow(biomeKey);
            if (FLAT.contains(PART)) {
                // Flat, so structures that refuse slopes and shores always have somewhere to stand.
                FlatLevelGeneratorSettings flat = new FlatLevelGeneratorSettings(Optional.of(HolderSet.direct()), biome, List.of());
                flat.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
                flat.getLayersInfo().add(new FlatLayerInfo(59, desert ? Blocks.SANDSTONE : Blocks.STONE));
                flat.getLayersInfo().add(new FlatLayerInfo(3, desert ? Blocks.SAND : Blocks.DIRT));
                flat.getLayersInfo().add(new FlatLayerInfo(1, desert ? Blocks.SAND : Blocks.GRASS_BLOCK));
                flat.updateLayers();
                return dimensions.replaceOverworldGenerator(registries, new FlatLevelSource(flat));
            }
            Holder<NoiseGeneratorSettings> noise = registries.lookupOrThrow(Registries.NOISE_SETTINGS).getOrThrow(NoiseGeneratorSettings.OVERWORLD);
            return dimensions.replaceOverworldGenerator(registries, new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), noise));
        })).create()) {
            world.getConnection().waitForChunksRender();
            world.getServer().runOnServer(server -> {
                ServerLevel level = server.overworld();
                level.getGameRules().set(GameRules.ADVANCE_TIME, false, server);
                level.getGameRules().set(GameRules.SPAWN_MOBS, false, server);
                level.getGameRules().set(GameRules.SHOW_ADVANCEMENT_MESSAGES, false, server);
                level.getGameRules().set(GameRules.SEND_COMMAND_FEEDBACK, false, server);
                run(server, "weather clear");
                run(server, "time set noon");
                var player = server.getPlayerList().getPlayers().get(0);
                int x = player.getBlockX();
                int z = player.getBlockZ();
                origin = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
                String ground = desert ? "sand" : "grass_block";
                run(server, fill(-16, -5, -16, 16, -2, 16, desert ? "sandstone" : "dirt"));
                run(server, fill(-16, -1, -16, 16, -1, 16, ground));
                run(server, fill(-16, 0, -16, 16, 14, 16, "air"));
                run(server, "gamemode spectator @a");
                player.getInventory().clearContent();
            });
            context.waitTicks(40);

            switch (PART) {
                case "structures" -> structures(context, world);
                case "portals" -> portals(context, world);
                case "plants" -> plants(context, world);
                case "floatingislands" -> floatingIslands(context, world);
                case "terrain" -> terrain(context, world);
                case "elevators" -> elevators(context, world);
                case "bridges" -> bridges(context, world);
                case "gravity" -> gravity(context, world);
                case "extruder" -> extruder(context, world);
                case "sensors" -> sensors(context, world);
                case "redstone" -> redstone(context, world);
                case "alarm" -> alarm(context, world);
                case "fan" -> fan(context, world);
                case "spikes" -> spikes(context, world);
                case "sodas" -> sodas(context, world);
                case "health" -> health(context, world);
                case "dragonfruit" -> dragonFruit(context, world);
                case "kitchen" -> kitchen(context, world);
                default -> throw new IllegalStateException("no scene for " + PART);
            }
        }
    }

    // ---- World ----

    private static void structures(ClientGameTestContext context, TestSingleplayerContext world) {
        int x0 = (((origin.getX() >> 4) - 1) << 4) - origin.getX();
        int z0 = (((origin.getZ() >> 4) - 4) << 4) - origin.getZ();
        commands(world, "place structure assortedstructures:fountain " + (origin.getX() + x0) + " " + origin.getY() + " " + (origin.getZ() + z0));
        context.waitTicks(40);
        int[] b = bounds(world, x0 - 16, z0 - 16, x0 + 48, z0 + 48, 0);
        System.out.println("LOGO fountain " + java.util.Arrays.toString(b));
        double cx = (b[0] + b[1]) / 2.0 + 0.5;
        double cz = (b[4] + b[5]) / 2.0 + 0.5;
        double h = b[3] + 1;
        shoot(context, world, cx + 8, h * 0.5, b[5] + 20, at(cx, h * 0.62, cz), true, "structures");
    }

    private static void portals(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, "time set midnight", fill(-7, -1, -10, 7, -1, 2, "end_stone"), fill(-3, -2, -7, 3, -1, -4, "end_stone_bricks"),
                block(-2, -1, -1, "assortedportals:void_crystal_ore"), block(3, -1, -3, "assortedportals:void_crystal_ore"), block(1, -1, 1, "assortedportals:void_crystal_ore"),
                block(-4, -1, -6, "assortedportals:void_crystal_ore"),
                fill(-1, 0, -5, 1, 0, -5, "assortedportals:void_portal_frame[facing=south]"),
                fill(-1, 4, -5, 1, 4, -5, "assortedportals:void_portal_frame[facing=south]"),
                fill(-2, 1, -5, -2, 3, -5, "assortedportals:void_portal_frame[facing=south]"),
                fill(2, 1, -5, 2, 3, -5, "assortedportals:void_portal_frame[facing=south]"),
                block(0, 1, -5, "assortedportals:ender_flames"),
                block(-4, 0, -3, "assortedportals:ender_flames"), block(4, 0, -2, "assortedportals:ender_flames"), block(-3, 0, -8, "assortedportals:ender_flames"));
        shoot(context, world, 3.0, 2.4, 5.0, at(0.5, 2.5, -4.5), true, "portals");
    }

    private static void plants(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-10, -1, -7, 10, -1, -5, "water"), fill(-10, -1, -4, 10, -1, -4, "sand"), fill(-10, -1, -8, 10, -1, -8, "sand"),
                reeds(-5, -4, 3), reeds(-4, -4, 2), reeds(-2, -4, 3), reeds(1, -4, 3), reeds(2, -4, 2), reeds(4, -4, 3), reeds(6, -4, 2),
                reeds(-6, -8, 2), reeds(-1, -8, 3), reeds(3, -8, 2), reeds(-3, -8, 3), reeds(5, -8, 3),
                // A netherrack overhang over the camera for the glowstone seeds to hang from, one of them ripe.
                fill(-10, 5, -3, 10, 6, 4, "netherrack"), fill(-10, 4, -3, -6, 4, 4, "netherrack"), fill(6, 4, -3, 10, 4, 4, "netherrack"),
                block(-4, 4, -3, "assortedplants:glowstone_seeds"), block(-1, 4, -2, "assortedplants:glowstone_seeds"),
                block(2, 4, -3, "assortedplants:glowstone_seeds"), block(4, 4, -2, "glowstone"), block(0, 4, -3, "assortedplants:glowstone_seeds"));
        shoot(context, world, 0.5, 1.4, 3.4, at(0.5, 3.7, -5.0), true, "plants");
    }

    private static String reeds(int x, int z, int height) {
        return fill(x, 0, z, x, height - 1, z, "assortedplants:gunpowder_reed");
    }

    private static void floatingIslands(ClientGameTestContext context, TestSingleplayerContext world) {
        int[][] spots = {{0, -46}, {-34, -70}, {32, -64}, {-12, -100}, {22, -104}};
        for (int[] spot : spots) {
            commands(world, "place feature assortedfloatingislands:floating_island " + (origin.getX() + spot[0]) + " " + origin.getY() + " " + (origin.getZ() + spot[1]));
        }
        context.waitTicks(20);
        int[] best = null;
        for (int[] spot : spots) {
            int[] b = bounds(world, spot[0] - 16, spot[1] - 16, spot[0] + 16, spot[1] + 16, 4);
            System.out.println("LOGO island at " + spot[0] + "," + spot[1] + ": " + java.util.Arrays.toString(b));
            if (b != null && (best == null || b[1] - b[0] + b[5] - b[4] > best[1] - best[0] + best[5] - best[4])) {
                best = b;
            }
        }
        double cx = (best[0] + best[1]) / 2.0 + 0.5;
        double cz = (best[4] + best[5]) / 2.0 + 0.5;
        double cy = (best[2] + best[3]) / 2.0;
        double width = Math.max(best[1] - best[0], best[5] - best[4]) + 1;
        // Level with the island and close, looking a little down so the ground far below fills the bottom.
        shoot(context, world, cx + width * 0.7, cy + 3, cz + width * 1.9, at(cx, cy - 1, cz), true, "floatingislands");
    }

    /** minX maxX minY maxY minZ maxZ of every block at least minUp over the origin in a box, relative to it, or null. */
    private static int[] bounds(TestSingleplayerContext world, int x1, int z1, int x2, int z2, int minUp) {
        int[][] found = {null};
        world.getServer().runOnServer(server -> {
            ServerLevel level = server.overworld();
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    for (int y = minUp; y < 140; y++) {
                        if (!level.getBlockState(origin.offset(x, y, z)).isAir()) {
                            int[] b = found[0];
                            found[0] = b == null ? new int[]{x, x, y, y, z, z}
                                    : new int[]{Math.min(b[0], x), Math.max(b[1], x), Math.min(b[2], y), Math.max(b[3], y), Math.min(b[4], z), Math.max(b[5], z)};
                        }
                    }
                }
            }
        });
        return found[0];
    }

    private static void terrain(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, feature("assortedterrain:crop_field", 1, -10), feature("assortedterrain:tree_stumps", 4, -3), feature("assortedterrain:tree_stumps", 7, -7),
                feature("assortedterrain:tree_stumps", -2, -2), feature("assortedterrain:patch_melons", 7, -12), feature("assortedterrain:patch_melons", -7, -12),
                // A boulder with randomite showing on the faces towards the camera.
                fill(-7, 0, -7, -3, 1, -4, "stone"), fill(-6, 2, -7, -4, 2, -5, "stone"), block(-3, 1, -4, "air"), block(-7, 1, -4, "air"),
                block(-5, 0, -4, "assortedterrain:randomite_ore"), block(-4, 1, -4, "assortedterrain:randomite_ore"), block(-3, 0, -6, "assortedterrain:randomite_ore"),
                block(-5, 2, -5, "assortedterrain:randomite_ore"), block(-6, 1, -4, "assortedterrain:randomite_ore"), block(-3, 1, -5, "assortedterrain:randomite_ore"));
        shoot(context, world, 0.5, 4.2, 5.5, at(0.0, 0.3, -6.0), true, "terrain");
    }

    private static String feature(String id, int x, int z) {
        return "place feature " + id + " " + (origin.getX() + x) + " " + origin.getY() + " " + (origin.getZ() + z);
    }

    // ---- Tech ----

    private static void elevators(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-3, 0, -7, 3, 13, -7, "stone_bricks"), fill(-3, 0, -6, -2, 13, -2, "stone_bricks"), fill(2, 0, -6, 3, 13, -2, "stone_bricks"),
                fill(-1, 0, -6, 1, 0, -2, "air"), fill(-1, 6, -2, 1, 6, -2, "stone_brick_slab"),
                fill(-1, 0, -5, 1, 0, -3, "assortedelevators:elevator"),
                block(-2, 1, -4, "assortedelevators:elevator_landing"), block(-2, 7, -4, "assortedelevators:elevator_landing"),
                block(2, 2, -3, "lantern"), block(-1, 3, -6, "wall_torch[facing=south]"), block(1, 9, -6, "wall_torch[facing=south]"),
                summon("villager", 0.5, 1, -3.5, "VillagerData:{profession:\"minecraft:mason\",level:2,type:\"minecraft:plains\"}"));
        aim(world, 1.6, 4.6, 6.0, at(0.0, 4.4, -4.0));
        world.getConnection().waitForChunksRender();
        context.waitTicks(30);
        commands(world, block(-3, 7, -4, "redstone_block"));
        context.waitTicks(22);
        context.getInput().pressKey(options -> options.keyToggleGui);
        context.waitTicks(2);
        context.takeScreenshot("logo_elevators");
    }

    private static void bridges(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, "time set 12200", fill(-3, -9, -16, 3, -1, 16, "air"), fill(-3, -10, -16, 3, -10, 16, "lava"),
                bridge(-3, "laser"), bridge(-6, "accel"), bridge(-9, "death"), bridge(-12, "trick"));
        context.waitTicks(40);
        shoot(context, world, 0.5, 5.0, 6.0, at(0.5, -2.0, -6.0), true, "bridges");
    }

    /** A control in the left cliff's edge shooting east, powered from a buried redstone block. */
    private static String bridge(int z, String type) {
        return block(-4, -1, z, "assortedbridges:bridge_control_" + type + "[facing=east]") + "\n" + block(-4, -2, z, "redstone_block");
    }

    private static void gravity(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-2, -1, -6, 2, -1, -2, "assortedgravity:metal_mesh"),
                block(0, -2, -4, "redstone_block"), block(0, -1, -4, "assortedgravity:gravitor{Range:2,ShowRange:1b}"),
                fill(-5, 0, -7, -5, 0, -7, "stone_bricks"), block(-5, 1, -7, "assortedgravity:attractor{Range:1,ShowRange:1b}"), block(-5, 0, -8, "redstone_block"),
                fill(5, 0, -7, 5, 0, -7, "stone_bricks"), block(5, 1, -7, "assortedgravity:repulsor{Range:1,ShowRange:1b}"), block(5, 0, -8, "redstone_block"),
                summon("armor_stand", -2.5, 0, -1.5, "equipment:{feet:{id:\"assortedgravity:gravity_boots\",count:1}},ShowArms:1b,Rotation:[-20f,0f]"));
        String[] items = {"gold_ingot", "diamond", "apple", "iron_ingot", "emerald", "redstone"};
        for (int i = 0; i < items.length; i++) {
            double a = i * Math.PI * 2 / items.length;
            commands(world, "summon item " + (origin.getX() + 0.5 + Math.cos(a) * 1.2) + " " + (origin.getY() + 0.3 + (i % 3) * 0.5) + " " + (origin.getZ() - 3.5 + Math.sin(a) * 1.2)
                    + " {Item:{id:\"minecraft:" + items[i] + "\",count:1},PickupDelay:32767,Age:-32768}");
        }
        context.waitTicks(12);
        shoot(context, world, 0.5, 3.4, 4.0, at(0.5, 0.8, -4.0), true, "gravity");
    }

    private static void extruder(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-9, 0, -16, 9, 7, -5, "stone"), block(-5, 3, -5, "coal_ore"), block(4, 5, -5, "iron_ore"), block(6, 1, -5, "copper_ore"),
                fill(0, 0, -7, 0, 0, -5, "air"), fill(0, -1, -7, 0, -1, 4, "cobblestone"),
                "summon assortedextruder:extruder " + (origin.getX() + 0.5) + " " + origin.getY() + " " + (origin.getZ() - 5.2)
                        + " {Type:\"diamond\",Facing:\"north\",Running:1b,Fuel:0}");
        shoot(context, world, 2.8, 1.8, -1.2, at(0.5, 0.5, -5.2), true, "extruder");
    }

    private static void sensors(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-6, 0, -6, 6, 4, -6, "stone_bricks"),
                block(-3, 1, -6, "assortedsensors:mossy_cobblestone_sensor[facing=south]"), block(-3, 2, -6, "redstone_lamp"),
                block(0, 1, -6, "assortedsensors:emerald_sensor[facing=south]"), block(0, 2, -6, "redstone_lamp"),
                block(3, 1, -6, "assortedsensors:gold_sensor[facing=south]"), block(3, 2, -6, "redstone_lamp"),
                summon("zombie", -2.5, 0, -4.7, ""), summon("villager", 0.5, 0, -4.7, "VillagerData:{profession:\"minecraft:librarian\",level:2,type:\"minecraft:plains\"}"),
                "summon item " + (origin.getX() + 3.5) + " " + (origin.getY() + 0.1) + " " + (origin.getZ() - 4.9) + " {Item:{id:\"minecraft:diamond\",count:1},PickupDelay:32767,Age:-32768}");
        context.waitTicks(20);
        shoot(context, world, 0.5, 2.2, 2.8, at(0.5, 1.4, -5.5), true, "sensors");
    }

    private static void redstone(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, "time set midnight", fill(-5, 0, -5, 5, 3, -5, "stone_bricks"), fill(-5, 0, -4, 5, 0, -4, "polished_andesite"),
                // Levers on the back of the wall power the blocks the glowstone torches hang on.
                block(-3, 2, -6, "lever[face=wall,facing=north,powered=true]"), block(3, 2, -6, "lever[face=wall,facing=north,powered=true]"),
                block(-3, 2, -4, "assortedredstone:glowstone_wall_torch[facing=south]"), block(3, 2, -4, "assortedredstone:glowstone_wall_torch[facing=south]"),
                block(-1, 1, -4, "assortedredstone:flip_flop_torch[lit=true]"), block(1, 1, -4, "assortedredstone:flip_flop_torch[lit=false]"),
                block(0, 1, -4, "redstone_wire[east=side,west=side]"), block(0, 2, -5, "stone_button[face=wall,facing=south]"));
        context.waitTicks(10);
        shoot(context, world, 0.5, 2.0, -0.4, at(0.5, 1.6, -4.5), true, "redstone");
    }

    private static void alarm(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, "time set 14500", fill(-5, 0, -5, 5, 4, -5, "stone_bricks"), fill(-5, 5, -5, 5, 5, -5, "stone_brick_slab"), fill(-5, -1, -4, 5, -1, -1, "polished_andesite"),
                block(0, 0, -5, "iron_door[half=lower,facing=south,hinge=left]"), block(0, 1, -5, "iron_door[half=upper,facing=south,hinge=left]"),
                block(-2, 2, -5, "redstone_lamp"), block(2, 2, -5, "redstone_lamp"),
                block(0, 2, -4, "assortedalarm:alarm[facing=south]"), block(-4, 3, -4, "assortedalarm:alarm[facing=south]"), block(4, 3, -4, "assortedalarm:alarm[facing=south]"),
                block(1, 0, -4, "stone_button[face=wall,facing=south]"), block(-1, 0, -4, "heavy_weighted_pressure_plate"),
                block(-2, 2, -6, "redstone_block"), block(2, 2, -6, "redstone_block"), block(-4, 0, -4, "lantern"), block(4, 0, -4, "lantern"));
        shoot(context, world, 1.6, 1.7, -0.2, at(0.3, 2.0, -4.5), true, "alarm");
    }

    private static void fan(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-5, 0, -5, -5, 0, -3, "stone_bricks"), block(-5, 1, -4, "assortedfan:fan[facing=east,mode=blow]{Range:8,Mode:1}"),
                block(-5, 2, -4, "stone_brick_slab"),
                summon("chicken", -1.5, 1.2, -4.0, "Rotation:[90f,0f]"));
        String[] items = {"feather", "oak_leaves", "paper", "feather", "string"};
        for (int i = 0; i < items.length; i++) {
            commands(world, "summon item " + (origin.getX() - 3.0 + i * 1.3) + " " + (origin.getY() + 0.8 + (i % 2) * 0.6) + " " + (origin.getZ() - 4.0 + (i % 3 - 1) * 0.4)
                    + " {Item:{id:\"minecraft:" + items[i] + "\",count:1},PickupDelay:32767,Age:-32768}");
        }
        context.waitTicks(6);
        shoot(context, world, 1.5, 2.4, 1.8, at(-2.0, 1.2, -4.0), true, "fan");
    }

    private static void spikes(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, "time set 13600", fill(-4, -3, -12, 4, 4, 1, "stone_bricks"), fill(-2, 0, -12, 2, 3, 1, "air"),
                fill(-2, -2, -9, 2, -2, -2, "redstone_block"), fill(-3, 0, -9, -3, 2, -2, "stone_bricks"),
                spikeRow(-2, "iron"), spikeRow(-3, "gold"), spikeRow(-4, "diamond"), spikeRow(-5, "emerald"), spikeRow(-6, "amethyst"),
                spikeRow(-7, "copper"), spikeRow(-8, "netherite"), spikeRow(-9, "stone"),
                block(-2, 2, -3, "wall_torch[facing=east]"), block(2, 2, -3, "wall_torch[facing=west]"),
                block(-2, 2, -8, "wall_torch[facing=east]"), block(2, 2, -8, "wall_torch[facing=west]"),
                summon("zombie", 0.5, 0, -10.5, ""));
        context.waitTicks(20);
        shoot(context, world, 0.5, 2.3, 0.4, at(0.5, 0.0, -6.0), true, "spikes");
    }

    private static String spikeRow(int z, String material) {
        return fill(-2, 0, z, 2, 0, z, "assortedspikes:" + material + "_spike[facing=up]");
    }

    // ---- Cuisine ----

    private static void sodas(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-5, 0, -6, 5, 3, -6, "spruce_planks"), fill(-4, 1, -5, 4, 1, -5, "spruce_slab[type=top]"), fill(-4, 2, -5, 4, 2, -5, "air"),
                fill(-4, 0, -3, 4, 0, -3, "stripped_spruce_log[axis=x]"),
                block(-4, 0, -5, "cauldron"), block(4, 0, -5, "brewing_stand"));
        String[] sodas = {"soda_orange", "soda_apple", "soda_root_beer", "soda_golden_apple", "soda_cocoa", "soda_diamond", "soda_slurm"};
        for (int i = 0; i < sodas.length; i++) {
            commands(world, display("assortedsodas:" + sodas[i], -2.5 + i * 0.85 + 0.5, 1.33, -2.5, 0f, 0.62f));
        }
        commands(world, display("assortedsodas:soda_cream_orange", -2.0, 2.3, -4.6, 0f, 0.55f), display("assortedsodas:soda_mushroom", -0.6, 2.3, -4.6, 0f, 0.55f),
                display("assortedsodas:soda_spiked_orange", 0.8, 2.3, -4.6, 0f, 0.55f), display("assortedsodas:soda_co2", 2.2, 2.3, -4.6, 0f, 0.55f));
        shoot(context, world, 0.5, 1.9, 1.3, at(0.5, 1.5, -3.0), true, "sodas");
    }

    private static void health(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-5, 0, -7, 5, 3, -7, "white_concrete"), fill(-5, 0, -7, 5, 0, -7, "red_concrete"),
                fill(-3, 0, -3, 3, 0, -3, "white_concrete"), fill(-5, -1, -7, 5, -1, 0, "smooth_quartz"),
                block(-4, 0, -6, "red_bed[facing=south,part=head]"), block(-4, 0, -5, "red_bed[facing=south,part=foot]"),
                block(4, 0, -6, "red_bed[facing=south,part=head]"), block(4, 0, -5, "red_bed[facing=south,part=foot]"),
                summon("villager", 0.5, 0, -5.0, "VillagerData:{profession:\"minecraft:cleric\",level:3,type:\"minecraft:plains\"}"));
        commands(world, display("assortedhealth:healthpack_super", 0.5, 1.45, -2.5, 0f, 0.8f), display("assortedhealth:healthpack", -1.1, 1.35, -2.5, 0f, 0.62f),
                display("assortedhealth:healthpack", 2.1, 1.35, -2.5, 0f, 0.62f), display("assortedhealth:bandage", -2.3, 1.3, -2.5, 0f, 0.55f),
                display("assortedhealth:sweets", 3.2, 1.3, -2.5, 0f, 0.5f), display("assortedhealth:powered_sweets", -3.2, 1.3, -2.5, 0f, 0.5f));
        shoot(context, world, 0.5, 1.9, 1.3, at(0.5, 1.5, -3.0), true, "health");
    }

    private static void dragonFruit(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, cactus(-3, -5, 3), cactus(3, -6, 3), cactus(0, -9, 2), cactus(-6, -10, 3), cactus(6, -3, 2), block(-1, 0, -3, "dead_bush"),
                display("assorteddragonfruit:dragon_fruit", 0.5, 0.45, -2.2, 0f, 0.85f),
                display("assorteddragonfruit:dragon_fruit", -1.9, 0.33, -3.7, 20f, 0.6f),
                display("assorteddragonfruit:dragon_fruit", 2.7, 0.33, -4.2, -20f, 0.6f));
        shoot(context, world, 0.5, 1.5, 1.2, at(0.5, 0.9, -4.0), true, "dragonfruit");
    }

    private static String cactus(int x, int z, int height) {
        return fill(x, 0, z, x, height - 1, z, "cactus");
    }

    private static void kitchen(ClientGameTestContext context, TestSingleplayerContext world) {
        commands(world, fill(-5, 0, -6, 5, 3, -6, "bricks"), fill(-4, 0, -4, 4, 0, -4, "smooth_stone"), block(-3, 0, -5, "furnace[facing=south,lit=true]"), block(3, 0, -5, "smoker[facing=south]"));
        String[] items = {"assortedkitchen:cheese", "assortedkitchen:butter", "assortedkitchen:chocolate_bar", "assortedkitchen:apple_pie"};
        for (int i = 0; i < items.length; i++) {
            commands(world, display(items[i], -1.2 + i * 1.1, 1.35, -3.5, 0f, 0.62f));
        }
        shoot(context, world, 0.5, 1.9, 1.3, at(0.5, 1.4, -3.5), true, "kitchen");
    }

    // ---- Helpers ----

    /** Puts the camera where it is wanted, hides the HUD unless the shot is of it, and screenshots. */
    private static void shoot(ClientGameTestContext context, TestSingleplayerContext world, double x, double y, double z, double[] at, boolean hideGui, String name) {
        aim(world, x, y, z, at);
        world.getConnection().waitForChunksRender();
        context.waitTicks(30);
        if (hideGui) {
            context.getInput().pressKey(options -> options.keyToggleGui);
        }
        context.waitTicks(5);
        context.takeScreenshot("logo_" + name);
    }

    /** Puts the eye, not the feet, at x y z, looking straight at the point: tp's own facing aims from the feet. */
    private static void aim(TestSingleplayerContext world, double x, double y, double z, double[] at) {
        double dx = at[0] - x;
        double dy = at[1] - y;
        double dz = at[2] - z;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        commands(world, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f %.2f %.2f", origin.getX() + x, origin.getY() + y - 1.62, origin.getZ() + z, yaw, pitch));
    }

    /** The highest surface in a box relative to the origin, ignoring anything lower than minUp above it. */
    private static BlockPos peak(TestSingleplayerContext world, int x1, int z1, int x2, int z2, int minUp) {
        BlockPos[] found = {origin};
        world.getServer().runOnServer(server -> {
            ServerLevel level = server.overworld();
            int best = Integer.MIN_VALUE;
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    int wx = origin.getX() + x;
                    int wz = origin.getZ() + z;
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, wx, wz);
                    if (y > best && y >= origin.getY() + minUp) {
                        best = y;
                        found[0] = new BlockPos(wx, y, wz);
                    }
                }
            }
        });
        return found[0];
    }

    private static double[] at(double x, double y, double z) {
        return new double[]{x, y, z};
    }

    private static String display(String item, double x, double y, double z, float yaw, float scale) {
        return String.format(Locale.ROOT, "summon item_display %.3f %.3f %.3f {item:{id:\"%s\",count:1},item_display:\"fixed\",Rotation:[%.1ff,0f],"
                        + "transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[%.2ff,%.2ff,%.2ff]}}",
                origin.getX() + x, origin.getY() + y, origin.getZ() + z, item, yaw + 180f, scale, scale, scale);
    }

    private static String summon(String entity, double x, double y, double z, String nbt) {
        String tag = "NoAI:1b,Invulnerable:1b,PersistenceRequired:1b,Silent:1b" + (nbt.isEmpty() ? "" : "," + nbt);
        if (!nbt.contains("Rotation")) {
            tag += ",Rotation:[0f,0f]";
        }
        return String.format(Locale.ROOT, "summon %s %.3f %.3f %.3f {%s}", entity, origin.getX() + x, origin.getY() + y, origin.getZ() + z, tag);
    }

    private static String block(int x, int y, int z, String state) {
        return "setblock " + (origin.getX() + x) + " " + (origin.getY() + y) + " " + (origin.getZ() + z) + " " + state;
    }

    private static String fill(int x1, int y1, int z1, int x2, int y2, int z2, String block) {
        return "fill " + (origin.getX() + x1) + " " + (origin.getY() + y1) + " " + (origin.getZ() + z1) + " "
                + (origin.getX() + x2) + " " + (origin.getY() + y2) + " " + (origin.getZ() + z2) + " " + block;
    }

    private static void commands(TestSingleplayerContext world, String... commands) {
        world.getServer().runOnServer(server -> {
            for (String command : commands) {
                for (String line : command.split("\n")) {
                    run(server, line);
                }
            }
        });
    }

    private static void run(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
    }
}
