package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.block.LuckyBlocks;
import fr.hugman.lucky_block.api.loot.LuckyBlockLootTables;
import fr.hugman.lucky_block.api.lucky_event.*;
import fr.hugman.lucky_block.api.lucky_event.selector.*;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.concurrent.CompletableFuture;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockEventProvider extends FabricDynamicRegistryProvider {
    private static final int[] LUCK_DISTRIBUTION = {3, 15, 10, 15, 3};
    private static final int[] LUCKY_LUCK_DISTRIBUTION = {1, 3, 10, 20, 5};
    private static final int[] VERY_LUCKY_LUCK_DISTRIBUTION = {1, 5, 15, 20};
    private static final int[] UNLUCKY_LUCK_DISTRIBUTION = {5, 20, 10, 3, 1};
    private static final int[] VERY_UNLUCKY_LUCK_DISTRIBUTION = {20, 15, 5, 3};

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

        // Set Blocks
        registerable.register(LuckyEvents.SET_BEDROCK, new SetBlockLuckyEvent(Blocks.BEDROCK));
        registerable.register(LuckyEvents.SET_RANDOM_LUCKY_BLOCK, new SetBlockLuckyEvent(
                LuckyBlocks.SUPER_LUCKY_BLOCK,
                LuckyBlocks.VERY_LUCKY_BLOCK,
                LuckyBlocks.UNLUCKY_BLOCK,
                LuckyBlocks.VERY_UNLUCKY_BLOCK,
                LuckyBlocks.DOUBLE_LUCKY_BLOCK,
                LuckyBlocks.TRIPLE_LUCKY_BLOCK
        ));
        registerable.register(LuckyEvents.SET_ORE_BLOCK, new SetBlockLuckyEvent(
                Blocks.COAL_ORE,
                Blocks.IRON_ORE,
                Blocks.GOLD_ORE,
                Blocks.REDSTONE_ORE,
                Blocks.LAPIS_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE
        ));

        // Pillars
        registerable.register(LuckyEvents.SET_BEDROCK_WORLD_PILLAR, new PillarLuckyEvent(Blocks.BEDROCK, true, true));
        registerable.register(LuckyEvents.SET_WOOL_PILLAR, new PillarLuckyEvent(new WeightedBlockStateProvider(new Pool.Builder<BlockState>()
                .add(Blocks.WHITE_WOOL.getDefaultState())
                .add(Blocks.ORANGE_WOOL.getDefaultState())
                .add(Blocks.MAGENTA_WOOL.getDefaultState())
                .add(Blocks.LIGHT_BLUE_WOOL.getDefaultState())
                .add(Blocks.YELLOW_WOOL.getDefaultState())
                .add(Blocks.LIME_WOOL.getDefaultState())
                .add(Blocks.PINK_WOOL.getDefaultState())
                .add(Blocks.GRAY_WOOL.getDefaultState())
                .add(Blocks.LIGHT_GRAY_WOOL.getDefaultState())
                .add(Blocks.CYAN_WOOL.getDefaultState())
                .add(Blocks.PURPLE_WOOL.getDefaultState())
                .add(Blocks.BLUE_WOOL.getDefaultState())
                .add(Blocks.BROWN_WOOL.getDefaultState())
                .add(Blocks.GREEN_WOOL.getDefaultState())
                .add(Blocks.RED_WOOL.getDefaultState())
                .add(Blocks.BLACK_WOOL.getDefaultState())
        ), true, true));

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
        registerable.register(LuckyEvents.SUMMON_GIANT, new SummonEntityLuckyEvent(EntityType.GIANT));
        registerable.register(LuckyEvents.SUMMON_CHARGED_CREEPER, AllOfSelectorLuckyEvent.builder()
                .add(SummonEntityLuckyEvent.builder(EntityType.CREEPER).shouldTarget().build())
                .add(new SummonEntityLuckyEvent(EntityType.LIGHTNING_BOLT))
                .build());
        registerable.register(LuckyEvents.SUMMON_SLIME, OneOfSelectorLuckyEvent.builder()
                .add(RepeatSelectorLuckyEvent.builder().count(1, 3).add(new SummonEntityLuckyEvent(EntityType.SLIME)).build())
                .add(RepeatSelectorLuckyEvent.builder().count(1, 2).add(new SummonEntityLuckyEvent(EntityType.MAGMA_CUBE)).build())
                .build());

        // Loots
        registerable.register(LuckyEvents.LOOT_LUCKY_SWORD, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_SWORD));
        registerable.register(LuckyEvents.LOOT_LUCKY_BOW, new LootLuckyEvent(LuckyBlockLootTables.LUCKY_BOW));
        registerable.register(LuckyEvents.LOOT_ALL_DYES, new LootLuckyEvent(LuckyBlockLootTables.ALL_DYES));
        registerable.register(LuckyEvents.LOOT_END_GAME_ITEM, new LootLuckyEvent(LuckyBlockLootTables.END_GAME_ITEM));
        registerable.register(LuckyEvents.LOOT_ELYTRA, new LootLuckyEvent(LuckyBlockLootTables.ELYTRA));
        registerable.register(LuckyEvents.LOOT_BUCKETS, new LootLuckyEvent(LuckyBlockLootTables.BUCKETS));
        registerable.register(LuckyEvents.LOOT_FISH_BUCKET, new LootLuckyEvent(LuckyBlockLootTables.FISH_BUCKET));
        registerable.register(LuckyEvents.LOOT_ROTTEN_FLESH, new LootLuckyEvent(LuckyBlockLootTables.ROTTEN_FLESH));

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
