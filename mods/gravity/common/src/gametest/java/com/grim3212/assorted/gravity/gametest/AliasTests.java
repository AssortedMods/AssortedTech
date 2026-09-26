package com.grim3212.assorted.gravity.gametest;

import com.google.gson.JsonParser;
import com.grim3212.assorted.gravity.Family;
import com.grim3212.assorted.gravity.common.block.GravityBlocks;
import com.grim3212.assorted.gravity.common.block.blockentity.GravityBlockEntityTypes;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A world saved when this was all one mod, Assorted Tech, still has these blocks, boots and gravity fields in it. */
final class AliasTests {

    private AliasTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("assortedtech_ids_still_load", AliasTests::assortedtechIdsStillLoad);
    }

    private static void assortedtechIdsStillLoad(GameTestHelper helper) {
        // The boots register through GravityBlocks.ITEMS too, so this is every item.
        for (IRegistryObject<Item> item : GravityBlocks.ITEMS.getEntries()) {
            ItemStack stack = ItemStack.CODEC.parse(helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    JsonParser.parseString("{\"id\": \"" + old(item.getId()) + "\", \"count\": 1}")).getOrThrow();
            helper.assertTrue(stack.is(item.get()), "a stack saved as " + old(item.getId()) + " reads back as " + stack);
        }

        for (IRegistryObject<Block> block : GravityBlocks.BLOCKS.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK.getValue(old(block.getId())), block.get(), "the block saved as " + old(block.getId()));
        }

        for (IRegistryObject<BlockEntityType<?>> type : GravityBlockEntityTypes.BLOCK_ENTITIES.getEntries()) {
            helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(old(type.getId())), type.get(), "the block entity saved as " + old(type.getId()));
        }
        helper.succeed();
    }

    private static Identifier old(Identifier id) {
        return Identifier.fromNamespaceAndPath(Family.ID, id.getPath());
    }
}
