package com.grim3212.assorted.elevators.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.elevators.Constants;
import com.grim3212.assorted.elevators.common.block.CamouflageBlock;
import com.grim3212.assorted.elevators.common.handlers.ElevatorsCreativeItems;
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

import static com.grim3212.assorted.lib.test.TestSupport.blockstateVariants;
import static com.grim3212.assorted.elevators.gametest.ElevatorsTestSupport.*;

/**
 * What the mod ships: models and names, loader keys both loaders read, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assets_have_models_and_names", AssetTests::assetsHaveModelsAndNames);
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

        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(ElevatorsCreativeItems.TAB)) {
            missing.add("the " + ElevatorsCreativeItems.TAB.location() + " creative tab");
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
    private static final String CAMOUFLAGE_LOADER = Constants.MOD_ID + ":camouflage";

    /**
     * Every variant of a camouflaged block's blockstate reaches a camouflage loader model, named by
     * the one {@code "loader"} key both loaders read in 1.21.1 (Fabric through AssortedLib). A
     * blockstate pointing at the plain model draws it whatever the disguise, and the 26.x keys - a
     * variant {@code "type"}, a model's {@code "fabric:type"} - are read by neither loader here, so a
     * json still leaning on one is wrong.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        List<String> checkedModels = new ArrayList<>();
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

            boolean camouflaged = entry.getValue() instanceof CamouflageBlock;
            for (JsonObject variant : blockstateVariants(blockstate)) {
                if (variant.has("type") || variant.has("fabric:type")) {
                    wrong.add("blockstate " + id.getPath() + " still has the 26.x key " + (variant.has("type") ? "type" : "fabric:type"));
                }

                String model = variant.has("model") ? variant.get("model").getAsString() : null;
                if (model == null || checkedModels.contains(model) || !model.startsWith(Constants.MOD_ID + ":")) {
                    continue;
                }
                checkedModels.add(model);

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
                    if (!CAMOUFLAGE_LOADER.equals(json.get("loader").getAsString())) {
                        wrong.add("model " + model + " names loader " + json.get("loader"));
                    }
                } else if (camouflaged) {
                    wrong.add("camouflaged " + id.getPath() + " draws the plain model " + model);
                }
            }
        }

        helper.assertTrue(loaderModels > 0, "no camouflage loader models were found to check");
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
