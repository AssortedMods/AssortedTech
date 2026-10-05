package com.grim3212.assorted.extruder.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.extruder.Constants;
import com.grim3212.assorted.extruder.common.entity.ExtruderEntities;
import com.grim3212.assorted.extruder.common.item.ExtruderItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Tech, still has its extruders and their items in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedtech_ids_still_load", AliasTests::assortedtechIdsStillLoad);
    }

    private static void assortedtechIdsStillLoad(GameTestHelper helper) {
        for (IRegistryObject<EntityType<?>> entity : ExtruderEntities.ENTITIES.getEntries()) {
            // The id a chunk saved an entity under, read back the way the world reads it.
            CompoundTag saved = new CompoundTag();
            saved.putString("id", old(entity.getId()).toString());
            ProblemReporter.Collector problems = new ProblemReporter.Collector();
            helper.assertValueEqual(EntityType.by(TagValueInput.create(problems, helper.getLevel().registryAccess(), saved)).orElse(null), entity.get(),
                    "the entity saved as " + old(entity.getId()));
        }

        for (IRegistryObject<Item> item : ExtruderItems.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, id.getPath());
    }
}
