package fr.hugman.lucky_block.data.provider;

import fr.hugman.lucky_block.api.lucky_event.LuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.LuckyEventTags;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static fr.hugman.lucky_block.api.lucky_event.LuckyEventTags.*;
import static fr.hugman.lucky_block.api.lucky_event.LuckyEventTags.UNLUCKY;
import static fr.hugman.lucky_block.api.lucky_event.LuckyEvents.*;

public class LuckyBlockEventTagProvider extends FabricTagProvider<LuckyEvent> {
    public LuckyBlockEventTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, LuckyBlockRegistryKeys.LUCKY_EVENT, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        builder(NORMAL).add(
                SUMMON_RAINBOW_SHEEP,
                SUMMON_HAPPY_GHAST,
                LOOT_ALL_DYES
        )
                .addOptional(SUMMON_ONE_WIND_CHARGE)
                .addTag(UNLUCKY)
                .addTag(LUCKY);

        builder(LUCKY).add(
                LOOT_LUCKY_SWORD,
                LOOT_LUCKY_BOW,
                LOOT_SADDLE,
                LOOT_END_GAME_ITEM
        );

        builder(UNLUCKY).add(
                SUMMON_CREEPER,
                SUMMON_GHAST
        )
                .addOptional(SUMMON_BOB)
                .addOptional(SUMMON_ONE_TNT);
    }
}