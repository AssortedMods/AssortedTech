package com.grim3212.assorted.redstone.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.redstone.Family;
import com.grim3212.assorted.redstone.common.block.RedstoneBlocks;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Tech, still has these torches in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedtech_ids_still_load", AliasTests::assortedtechIdsStillLoad);
    }

    private static void assortedtechIdsStillLoad(GameTestHelper helper) {
        for (IRegistryObject<Item> item : RedstoneBlocks.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<Block> block : RedstoneBlocks.BLOCKS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(old(block.getId())), block.get(), "the block saved as " + old(block.getId()));
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Family.ID, id.getPath());
    }
}
