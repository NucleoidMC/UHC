package fr.hugman.lucky_block.data.provider;

import fr.hugman.lucky_block.api.loot.LuckyBlockLootTables;
import fr.hugman.lucky_block.api.lucky_event.*;
import fr.hugman.lucky_block.api.lucky_event.selector.AllOfSelectorLuckyEvent;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LuckyBlockEventProvider extends FabricDynamicRegistryProvider {
    public LuckyBlockEventProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        var registry = registries.getOrThrow(LuckyBlockRegistryKeys.LUCKY_EVENT);
        registry.streamKeys()
                .filter(registryKey -> registryKey.getValue().getNamespace().equals(LuckyBlockMod.MOD_ID))
                .map(key -> entries.add(registry, key))
                .toList();
    }

    @Override
    public String getName() {
        return "Lucky Events (Lucky)";
    }


    public static void register(Registerable<LuckyEvent> registerable) {
        var events = registerable.getRegistryLookup(LuckyBlockRegistryKeys.LUCKY_EVENT);

        // Summon Entities
        registerable.register(LuckyEvents.SUMMON_CREEPER, new SummonEntityLuckyEvent(mobData(EntityType.CREEPER)));
        registerable.register(LuckyEvents.SUMMON_RAINBOW_SHEEP, new SummonEntityLuckyEvent(mobData(EntityType.SHEEP, "_jeb")));
        registerable.register(LuckyEvents.SUMMON_GHAST, new SummonEntityLuckyEvent(mobData(EntityType.GHAST)));
        registerable.register(LuckyEvents.SUMMON_HAPPY_GHAST, new SummonEntityLuckyEvent(mobData(EntityType.HAPPY_GHAST)));

        // Loots
        registerable.register(LuckyEvents.LOOT_LUCKY_SWORD, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_SWORD));
        registerable.register(LuckyEvents.LOOT_LUCKY_BOW, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_BOW));
        registerable.register(LuckyEvents.LOOT_ALL_DYES, new LootLuckyEvent(LuckyBlockLootTables.ALL_DYES));
        registerable.register(LuckyEvents.LOOT_END_GAME_ITEM, new LootLuckyEvent(LuckyBlockLootTables.END_GAME_ITEM));
        registerable.register(LuckyEvents.LOOT_SADDLE, new LootLuckyEvent(LuckyBlockLootTables.SADDLE));

        // Pools
        registerable.register(LuckyEvents.POOL_NORMAL, new AllOfSelectorLuckyEvent(events.getOrThrow(LuckyEventTags.NORMAL)));
    }

    private static NbtComponent mobData(EntityType<?> entityType) {
        var compound = new NbtCompound();
        compound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        return NbtComponent.of(compound);
    }

    private static NbtComponent mobData(EntityType<?> entityType, String name) {
        var compound = new NbtCompound();
        compound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        compound.putString("CustomName", name);
        return NbtComponent.of(compound);
    }

    private static NbtComponent bobData() {
        var compound = new NbtCompound();

        compound.putString("id", "minecraft:zombie");
        compound.putString("CustomName", "Bob");
        compound.putString("equipment", "{\n" +
                "      \"head\": {\n" +
                "        \"id\": \"minecraft:diamond_helmet\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:protection\": 4,\n" +
                "            \"minecraft:unbreaking\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"chest\": {\n" +
                "        \"id\": \"minecraft:diamond_chestplate\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:protection\": 4,\n" +
                "            \"minecraft:unbreaking\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"legs\": {\n" +
                "        \"id\": \"minecraft:diamond_leggings\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:protection\": 4,\n" +
                "            \"minecraft:unbreaking\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"feet\": {\n" +
                "        \"id\": \"minecraft:diamond_boots\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:protection\": 4,\n" +
                "            \"minecraft:unbreaking\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"mainhand\": {\n" +
                "        \"id\": \"minecraft:diamond_sword\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:sharpness\": 5,\n" +
                "            \"minecraft:unbreaking\": 3,\n" +
                "            \"minecraft:fire_aspect\": 2\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"offhand\": {\n" +
                "        \"id\": \"minecraft:shield\",\n" +
                "        \"components\": {\n" +
                "          \"minecraft:enchantments\": {\n" +
                "            \"minecraft:unbreaking\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }");
        return NbtComponent.of(compound);
    }
}
