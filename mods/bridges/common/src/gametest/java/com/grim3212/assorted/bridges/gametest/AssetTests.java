package com.grim3212.assorted.bridges.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.bridges.Constants;
import com.grim3212.assorted.bridges.common.block.BridgesBlocks;
import com.grim3212.assorted.bridges.common.handlers.BridgesCreativeItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.bridges.gametest.BridgesTestSupport.*;

/**
 * What the mod ships: models and names, the bridge item model, loader keys both loaders read, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assets_have_models_and_names", AssetTests::assetsHaveModelsAndNames);
        out.accept("bridge_item_draws_its_stored_block", AssetTests::bridgeItemDrawsItsStoredBlock);
        out.accept("loader_models_are_read_on_both_loaders", AssetTests::loaderModelsAreReadOnBothLoaders);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
    }

    /**
     * Every block and item has a model and an English name, and the creative tab exists. Every gap
     * is reported at once.
     */
    private static void assetsHaveModelsAndNames(GameTestHelper helper) {
        JsonObject lang = readJson("/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        helper.assertTrue(lang != null, "assets/" + Constants.MOD_ID + "/lang/en_us.json is not on the classpath");

        List<String> missing = new ArrayList<>();
        int blocks = 0;
        int items = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            blocks++;
            if (!resourceExists("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstates/" + id.getPath() + ".json");
            }
            String key = entry.getValue().getDescriptionId();
            if (!lang.has(key)) {
                missing.add("lang key " + key + " (block " + id + ")");
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            items++;
            if (!resourceExists("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json")) {
                missing.add("items/" + id.getPath() + ".json");
            }
            String key = entry.getValue().getDescriptionId();
            if (!lang.has(key)) {
                missing.add("lang key " + key + " (item " + id + ")");
            }
        }

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(BridgesCreativeItems.TAB)) {
            missing.add("the " + BridgesCreativeItems.TAB.identifier() + " creative tab");
        }
        if (!lang.has("itemGroup." + Constants.FAMILY_ID)) {
            missing.add("lang key itemGroup." + Constants.FAMILY_ID);
        }

        // Guards against the whole walk passing because the registries came back empty.
        helper.assertTrue(blocks >= 6, "only " + blocks + " blocks are registered under " + Constants.MOD_ID);
        helper.assertTrue(items >= 6, "only " + items + " items are registered under " + Constants.MOD_ID);
        helper.assertTrue(missing.isEmpty(), missing.size() + " missing asset(s) across " + blocks + " blocks and "
                + items + " items: " + String.join(", ", missing));

        helper.succeed();
    }

    /**
     * The bridge item draws the block it holds: {@code items/bridge.json} must name the {@code
     * assortedbridges:bridge} item model type with its tint, and the block model it bakes must be a
     * bridge loader model on both loaders, as only that resolves a stack to its stored block. A plain
     * {@code minecraft:model}, or a block model without the loader, bakes the bridge once, empty, and
     * nothing warns. {@code BridgesClientGameTests} checks what it draws.
     */
    private static void bridgeItemDrawsItsStoredBlock(GameTestHelper helper) {
        JsonObject json = readJson("/assets/" + Constants.MOD_ID + "/items/bridge.json");
        helper.assertTrue(json != null, "assets/" + Constants.MOD_ID + "/items/bridge.json is not on the classpath");

        JsonObject model = json.getAsJsonObject("model");
        helper.assertValueEqual(stringOf(model, "type"), Constants.MOD_ID + ":bridge", "item model type of the bridge");
        helper.assertValueEqual(stringOf(model, "model"), Constants.MOD_ID + ":block/bridge", "block model the bridge item draws");
        helper.assertTrue(model.has("tints") && model.getAsJsonArray("tints").size() == 1
                        && (Constants.MOD_ID + ":bridge").equals(model.getAsJsonArray("tints").get(0).getAsJsonObject().get("type").getAsString()),
                "the bridge item lost its " + Constants.MOD_ID + ":bridge tint: " + model.get("tints"));

        Identifier blockModel = Identifier.parse(stringOf(model, "model"));
        JsonObject blockJson = readJson("/assets/" + blockModel.getNamespace() + "/models/" + blockModel.getPath() + ".json");
        helper.assertTrue(blockJson != null, "the bridge item draws " + blockModel + ", which does not exist");
        helper.assertValueEqual(stringOf(blockJson, "loader"), BRIDGE_LOADER, "the NeoForge loader of " + blockModel + ", which the bridge item draws");
        helper.assertValueEqual(stringOf(blockJson, "fabric:type"), BRIDGE_LOADER, "the Fabric loader of " + blockModel + ", which the bridge item draws");

        helper.succeed();
    }

    /** The loader this mod registers, spelled out: its class is client only. */
    private static final String BRIDGE_LOADER = Constants.MOD_ID + ":bridge";

    /** AssortedLib's blockstate model type, which bakes a loader model whole rather than flattening it. */
    private static final String SPECIFICATION = "assortedlib:specification";

    /**
     * Every variant of the bridge's blockstate reaches a bridge loader model, on both loaders.
     * NeoForge reads a variant's {@code "type"} and a model's {@code "loader"}, Fabric only {@code
     * "fabric:type"} for either, and the variant has to be AssortedLib's specification type to bake the
     * loader model whole. A blockstate pointing at a plain template, a plain variant, or a json with
     * only one loader's key bakes the empty bridge whatever is stored, and nothing warns.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        int loaderModels = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            JsonObject blockstate = readJson("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
            if (blockstate == null) {
                continue;
            }
            boolean custom = entry.getValue() == BridgesBlocks.BRIDGE.get();

            for (JsonObject variant : blockstateVariants(blockstate)) {
                String type = stringOf(variant, "type");
                String fabricType = stringOf(variant, "fabric:type");
                if ((type != null || fabricType != null) && !String.valueOf(type).equals(fabricType)) {
                    wrong.add("blockstate " + id.getPath() + " has type " + type + " but fabric:type " + fabricType);
                }

                String model = stringOf(variant, "model");
                if (model == null || !model.startsWith(Constants.MOD_ID + ":")) {
                    if (custom) {
                        wrong.add("blockstate " + id.getPath() + " has a variant naming no model of this mod: " + model);
                    }
                    continue;
                }

                Identifier modelId = Identifier.parse(model);
                JsonObject json = readJson("/assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json");
                if (json == null) {
                    wrong.add("blockstate " + id.getPath() + " names " + model + ", which does not exist");
                    continue;
                }
                String neoForgeLoader = stringOf(json, "loader");
                String fabricLoader = stringOf(json, "fabric:type");
                if (neoForgeLoader != null || fabricLoader != null) {
                    loaderModels++;
                    if (!BRIDGE_LOADER.equals(neoForgeLoader) || !BRIDGE_LOADER.equals(fabricLoader)) {
                        wrong.add("model " + model + " has loader " + neoForgeLoader + " and fabric:type " + fabricLoader + ", not " + BRIDGE_LOADER);
                    }
                    if (!SPECIFICATION.equals(type) || !SPECIFICATION.equals(fabricType)) {
                        wrong.add("blockstate " + id.getPath() + " reaches " + model + " through type " + type + " and fabric:type " + fabricType + ", not " + SPECIFICATION);
                    }
                } else if (custom) {
                    wrong.add("bridge variant model " + model + " is a plain model");
                }
            }
        }

        helper.assertTrue(loaderModels >= 2, "only " + loaderModels + " bridge loader models were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " json(s) would draw a static model: " + String.join("; ", wrong));
        helper.succeed();
    }

    private static String stringOf(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsString() : null;
    }

    /**
     * Every recipe file either loaded or was skipped by this loader's own load conditions; anything
     * else failed to parse.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }

    /**
     * Every non-vanilla item tag has a {@code tag.item.<namespace>.<path>} name, the check Fabric
     * API warns about at dev startup. Both loaders name the standard c: tags, so anything missing
     * is ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).getTags()
                .map(tag -> tag.key().location())
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }
}
