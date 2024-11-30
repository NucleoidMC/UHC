package com.hugman.lucky_block;

import com.google.common.reflect.Reflection;
import com.hugman.lucky_block.block.LuckyBlockInterface;
import com.hugman.lucky_block.block.LuckyBlocks;
import com.hugman.lucky_block.lucky_event.LuckyEventTypes;
import com.hugman.lucky_block.registry.LuckyBlockRegistries;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LuckyBlockMod implements ModInitializer {
    private static final String MOD_ID = "lucky_block";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        LuckyBlockRegistries.registerDynamics();
        Reflection.initialize(LuckyEventTypes.class);

        Reflection.initialize(LuckyBlocks.class);

        if (PolymerResourcePackUtils.addModAssets("uhc")) {
            LOGGER.info("Successfully added mod assets for " + MOD_ID);
        } else {
            LOGGER.error("Failed to add mod assets for " + MOD_ID);
        }
        PlayerBlockBreakEvents.AFTER.register((world, playerEntity, blockPos, blockState, blockEntity) -> {
            if(blockState.getBlock() instanceof LuckyBlockInterface lucky && world instanceof ServerWorld serverWorld) {
                lucky.onLuckyBlockBreak(serverWorld, playerEntity, blockPos, blockState, blockEntity);
            }
        });
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
