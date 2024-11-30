package com.hugman.lucky_block.registry;

import com.hugman.lucky_block.lucky_event.LuckyEvent;
import com.hugman.lucky_block.lucky_event.LuckyEventType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

public class LuckyBlockRegistries {
    public static final SimpleRegistry<LuckyEventType<?>> LUCKY_EVENT_TYPE = FabricRegistryBuilder.createSimple(LuckyBlockRegistryKeys.LUCKY_EVENT_TYPE).buildAndRegister();

    public static void registerDynamics() {
        DynamicRegistries.register(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyEvent.TYPE_CODEC);
    }
}
