package com.grim3212.assorted.redstone.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.redstone.Constants;
import com.grim3212.assorted.redstone.Family;
import com.grim3212.assorted.redstone.common.handlers.RedstoneCreativeItems;
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

import static com.grim3212.assorted.redstone.gametest.RedstoneTestSupport.*;

/**
 * What the mod ships: models and names, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assets_have_models_and_names", AssetTests::assetsHaveModelsAndNames);
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

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(RedstoneCreativeItems.TAB)) {
            missing.add("the " + RedstoneCreativeItems.TAB.identifier() + " creative tab");
        }
        if (!lang.has("itemGroup." + Family.ID)) {
            missing.add("lang key itemGroup." + Family.ID);
        }

        // Guards against the whole walk passing because the registries came back empty.
        helper.assertTrue(blocks >= 4, "only " + blocks + " blocks are registered under " + Constants.MOD_ID);
        helper.assertTrue(items >= 2, "only " + items + " items are registered under " + Constants.MOD_ID);
        helper.assertTrue(missing.isEmpty(), missing.size() + " missing asset(s) across " + blocks + " blocks and "
                + items + " items: " + String.join(", ", missing));

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
