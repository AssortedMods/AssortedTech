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
import net.minecraft.resources.ResourceLocation;
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
            ResourceLocation id = entry.getKey().location();
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
            ResourceLocation id = entry.getKey().location();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            items++;
            // 1.21.1 reads an item's model straight from models/item; the items/ definitions came with 1.21.4.
            if (!resourceExists("/assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json")) {
                missing.add("models/item/" + id.getPath() + ".json");
            }
            String key = entry.getValue().getDescriptionId();
            if (!lang.has(key)) {
                missing.add("lang key " + key + " (item " + id + ")");
            }
        }

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(BridgesCreativeItems.TAB)) {
            missing.add("the " + BridgesCreativeItems.TAB.location() + " creative tab");
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

    /** The loader this mod registers, spelled out: its class is client only. */
    private static final String BRIDGE_LOADER = Constants.MOD_ID + ":bridge";

    /**
     * The bridge item draws the block it holds: its item model must bake through the bridge loader,
     * its own or a parent's, as only that model resolves a stack to its stored block through its item
     * overrides. An item model that reaches a plain template bakes the bridge once, empty, and
     * nothing warns.
     */
    private static void bridgeItemDrawsItsStoredBlock(GameTestHelper helper) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(BridgesBlocks.BRIDGE.get().asItem());
        helper.assertTrue(readJson("/assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json") != null,
                "assets/" + id.getNamespace() + "/models/item/" + id.getPath() + ".json is not on the classpath");
        helper.assertValueEqual(inheritedLoader(id.withPrefix("item/")), BRIDGE_LOADER, "the loader the bridge item is drawn by");
        helper.succeed();
    }

    /** The {@code loader} a model json or the nearest of its parents in this mod names, or null. */
    private static String inheritedLoader(ResourceLocation model) {
        for (int depth = 0; depth < 8 && model != null && Constants.MOD_ID.equals(model.getNamespace()); depth++) {
            JsonObject json = readJson("/assets/" + model.getNamespace() + "/models/" + model.getPath() + ".json");
            if (json == null) {
                return null;
            }
            if (json.has("loader")) {
                return json.get("loader").getAsString();
            }
            model = json.has("parent") ? ResourceLocation.parse(json.get("parent").getAsString()) : null;
        }
        return null;
    }

    /**
     * Every variant of the bridge's blockstate reaches a bridge loader model, named by the one
     * {@code "loader"} key both loaders read in 1.21.1 (Fabric through AssortedLib). A blockstate
     * pointing at a plain template bakes it once and draws the empty bridge whatever is stored, and
     * the 26.x keys - a variant {@code "type"}, a model's {@code "fabric:type"} - are read by neither
     * loader here, so a json still leaning on one is wrong.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        int loaderModels = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            JsonObject blockstate = readJson("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
            if (blockstate == null) {
                continue;
            }

            boolean isBridge = entry.getValue() == BridgesBlocks.BRIDGE.get();
            for (JsonObject variant : blockstateVariants(blockstate)) {
                if (variant.has("type") || variant.has("fabric:type")) {
                    wrong.add("blockstate " + id.getPath() + " still has the 26.x key " + (variant.has("type") ? "type" : "fabric:type"));
                }

                String model = variant.has("model") ? variant.get("model").getAsString() : null;
                if (model == null || !model.startsWith(Constants.MOD_ID + ":")) {
                    continue;
                }
                ResourceLocation modelId = ResourceLocation.parse(model);
                JsonObject json = readJson("/assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json");
                if (json == null) {
                    wrong.add("blockstate " + id.getPath() + " names " + model + ", which does not exist");
                    continue;
                }
                if (json.has("fabric:type")) {
                    wrong.add("model " + model + " still has the 26.x key fabric:type");
                }
                if (json.has("loader")) {
                    loaderModels++;
                    if (!BRIDGE_LOADER.equals(json.get("loader").getAsString())) {
                        wrong.add("model " + model + " names loader " + json.get("loader"));
                    }
                } else if (isBridge) {
                    wrong.add("bridge variant model " + model + " is a plain model");
                }
            }
        }

        helper.assertTrue(loaderModels >= 2, "only " + loaderModels + " bridge loader models were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " json(s) would draw a static model: " + String.join("; ", wrong));
        helper.succeed();
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
            ResourceLocation id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(id).isPresent()) {
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
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).listTags()
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
