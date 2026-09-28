package com.grim3212.assorted.grates.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.grates.Constants;
import com.grim3212.assorted.grates.api.util.GrateMaterial;
import com.grim3212.assorted.grates.common.block.GratesBlocks;
import com.grim3212.assorted.grates.common.block.ItemGrateBlock;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved with the metal mesh, from Assorted Tech 9.x or Assorted Gravity, has an iron item grate in its place. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("metal_mesh_ids_still_load", AliasTests::metalMeshIdsStillLoad);
    }

    private static void metalMeshIdsStillLoad(GameTestHelper helper) {
        ItemGrateBlock iron = GratesBlocks.ITEM_GRATES.get(GrateMaterial.IRON).get();

        for (String namespace : List.of(Constants.FAMILY_ID, "assortedgravity")) {
            Identifier mesh = Identifier.fromNamespaceAndPath(namespace, "metal_mesh");
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + mesh + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(iron.asItem()), "a stack saved as " + mesh + " reads back as " + stack);
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(mesh), iron, "the block saved as " + mesh);
        }
        helper.succeed();
    }
}
