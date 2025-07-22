package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.loot.LuckyBlockLootTables;
import fr.hugman.lucky_block.api.lucky_event.*;
import fr.hugman.lucky_block.api.lucky_event.selector.*;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockEventProvider extends FabricDynamicRegistryProvider {
    private static final int[] LUCK_DISTRIBUTION = {3,15,20,15,3};
    private static final int[] LUCKY_LUCK_DISTRIBUTION = {1,3,10,20,5};
    private static final int[] VERY_LUCKY_LUCK_DISTRIBUTION = {1,5,15,20};
    private static final int[] UNLUCKY_LUCK_DISTRIBUTION = {5,20,10,3,1};
    private static final int[] VERY_UNLUCKY_LUCK_DISTRIBUTION = {20,15,5,3};

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
        return "Lucky Events";
    }


    public static void register(Registerable<LuckyEvent> registerable) {
        var events = registerable.getRegistryLookup(LuckyBlockRegistryKeys.LUCKY_EVENT);

        // Summon Entities
        registerable.register(LuckyEvents.SUMMON_TAMED_CAT, SummonEntityLuckyEvent.builder(EntityType.CAT).tamed().build());
        registerable.register(LuckyEvents.SUMMON_TAMED_WOLF, SummonEntityLuckyEvent.builder(EntityType.WOLF).tamed().build());
        registerable.register(LuckyEvents.SUMMON_RAINBOW_SHEEP, SummonEntityLuckyEvent.builder(EntityType.SHEEP).name("jeb_").build());
        registerable.register(LuckyEvents.SUMMON_HAPPY_GHAST, OneOfSelectorLuckyEvent.builder()
                .add(summonHappyGhast(Items.WHITE_HARNESS))
                .add(summonHappyGhast(Items.ORANGE_HARNESS))
                .add(summonHappyGhast(Items.MAGENTA_HARNESS))
                .add(summonHappyGhast(Items.LIGHT_BLUE_HARNESS))
                .add(summonHappyGhast(Items.YELLOW_HARNESS))
                .add(summonHappyGhast(Items.LIME_HARNESS))
                .add(summonHappyGhast(Items.PINK_HARNESS))
                .add(summonHappyGhast(Items.GRAY_HARNESS))
                .add(summonHappyGhast(Items.LIGHT_GRAY_HARNESS))
                .add(summonHappyGhast(Items.CYAN_HARNESS))
                .add(summonHappyGhast(Items.PURPLE_HARNESS))
                .add(summonHappyGhast(Items.BLUE_HARNESS))
                .add(summonHappyGhast(Items.BROWN_HARNESS))
                .add(summonHappyGhast(Items.GREEN_HARNESS))
                .add(summonHappyGhast(Items.RED_HARNESS))
                .add(summonHappyGhast(Items.BLACK_HARNESS))
                .build());

        registerable.register(LuckyEvents.SUMMON_ANGRY_WOLF, SummonEntityLuckyEvent.builder(EntityType.WOLF).shouldTarget().build());
        registerable.register(LuckyEvents.SUMMON_CREEPER, SummonEntityLuckyEvent.builder(EntityType.CREEPER).shouldTarget().build());
        registerable.register(LuckyEvents.SUMMON_GHAST, new SummonEntityLuckyEvent(EntityType.GHAST));
        registerable.register(LuckyEvents.SUMMON_WARDEN, new SummonEntityLuckyEvent(EntityType.WARDEN));
        registerable.register(LuckyEvents.SUMMON_WITHER, new SummonEntityLuckyEvent(EntityType.WITHER));
        registerable.register(LuckyEvents.SUMMON_WITCH, AllOfSelectorLuckyEvent.builder()
                .add(SummonEntityLuckyEvent.builder(EntityType.WITCH).shouldTarget().build())
                .add(RepeatSelectorLuckyEvent.builder()
                        .count(3, 6)
                        .add(new SummonEntityLuckyEvent(EntityType.BAT))
                        .build())
                .build());

        // Loots
        registerable.register(LuckyEvents.LOOT_LUCKY_SWORD, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_SWORD));
        registerable.register(LuckyEvents.LOOT_LUCKY_BOW, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_BOW));
        registerable.register(LuckyEvents.LOOT_ALL_DYES, new LootLuckyEvent(LuckyBlockLootTables.ALL_DYES));
        registerable.register(LuckyEvents.LOOT_END_GAME_ITEM, new LootLuckyEvent(LuckyBlockLootTables.END_GAME_ITEM));
        registerable.register(LuckyEvents.LOOT_SADDLE, new LootLuckyEvent(LuckyBlockLootTables.SADDLE));

        // Pools
        registerable.register(LuckyPoolEvents.NORMAL, WeightedListSelectorLuckyEvent.builder(events)
                .add(LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.LUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(LUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(LUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(LUCKY_LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.VERY_LUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[0], 2, LuckyEventTags.UNLUCKY)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[1], 5, LuckyEventTags.NORMAL)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[2], 7, LuckyEventTags.LUCKY)
                .add(VERY_LUCKY_LUCK_DISTRIBUTION[3], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.UNLUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(UNLUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(UNLUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .add(UNLUCKY_LUCK_DISTRIBUTION[4], 12, LuckyEventTags.VERY_LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.VERY_UNLUCKY, WeightedListSelectorLuckyEvent.builder(events)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[1], 2, LuckyEventTags.UNLUCKY)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[2], 5, LuckyEventTags.NORMAL)
                .add(VERY_UNLUCKY_LUCK_DISTRIBUTION[3], 7, LuckyEventTags.LUCKY)
                .build()
        );
        registerable.register(LuckyPoolEvents.DOUBLE, RepeatSelectorLuckyEvent.builder().count(2).add(events.getOrThrow(LuckyPoolEvents.NORMAL)).build());
        registerable.register(LuckyPoolEvents.TRIPLE, RepeatSelectorLuckyEvent.builder().count(3).add(events.getOrThrow(LuckyPoolEvents.NORMAL)).build());
    }

    private static SummonEntityLuckyEvent summonHappyGhast(Item harness) {
        var compound = new NbtCompound();
        var equipment = new NbtCompound();
        var itemElement = new NbtCompound();
        itemElement.putString("id", Registries.ITEM.getId(harness).toString());
        equipment.put("body", itemElement);
        compound.put("equipment", equipment);
        return SummonEntityLuckyEvent.builder(EntityType.HAPPY_GHAST)
                .data(compound)
                .build();
    }

    private static SelectorLuckyEvent basicPool(int[] distribution, RegistryEntryLookup<LuckyEvent> lookup) {
        return WeightedListSelectorLuckyEvent.builder(lookup)
                .add(distribution[0], 0, LuckyEventTags.VERY_UNLUCKY)
                .add(distribution[1], 2, LuckyEventTags.UNLUCKY)
                .add(distribution[2], 5, LuckyEventTags.NORMAL)
                .add(distribution[3], 7, LuckyEventTags.LUCKY)
                .add(distribution[4], 12, LuckyEventTags.VERY_LUCKY)
                .build();
    }
}
