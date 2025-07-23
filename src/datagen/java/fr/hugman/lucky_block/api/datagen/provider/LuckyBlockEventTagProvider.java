package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static fr.hugman.lucky_block.api.lucky_event.LuckyEventTags.*;
import static fr.hugman.lucky_block.api.lucky_event.LuckyEvents.*;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockEventTagProvider extends FabricTagProvider<LuckyEvent> {
    public LuckyBlockEventTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, LuckyBlockRegistryKeys.LUCKY_EVENT, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(VERY_UNLUCKY).add(
                        SUMMON_WITHER,
                        SUMMON_WARDEN,
                        SUMMON_CHARGED_CREEPER
                )
                .addOptional(SUMMON_BOB);

        builder(UNLUCKY).add(
                SUMMON_CREEPER,
                SUMMON_GHAST,
                SUMMON_WITCH,
                SUMMON_SLIME,
                SUMMON_ANGRY_WOLF,
                SET_BEDROCK_WORLD_PILLAR,
                LOOT_ROTTEN_FLESH
        )
                .addOptional(SUMMON_ONE_WIND_CHARGE)
                .addOptional(SUMMON_ONE_TNT);

        builder(NORMAL).add(
                SUMMON_RAINBOW_SHEEP,
                LOOT_ALL_DYES,
                SET_BEDROCK,
                SET_RANDOM_LUCKY_BLOCK,
                SET_WOOL_PILLAR,
                SUMMON_GIANT,
                LOOT_BUCKETS,
                LOOT_FISH_BUCKET
        );

        builder(LUCKY).add(
                SUMMON_HAPPY_GHAST,
                SUMMON_TAMED_WOLF,
                SUMMON_TAMED_CAT,
                LOOT_LUCKY_SWORD,
                LOOT_LUCKY_BOW
        );

        builder(VERY_LUCKY).add(
                LOOT_END_GAME_ITEM,
                SET_ORE_BLOCK,
                LOOT_ELYTRA
        );
    }
}