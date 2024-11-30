package com.hugman.lucky_block.lucky_event;

import com.hugman.lucky_block.LuckyBlockMod;
import com.hugman.lucky_block.registry.LuckyBlockRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LuckyEventTypes {
    public static final LuckyEventType<SummonEntityLuckyEvent> SUMMON_ENTITY = register("summon_entity", SummonEntityLuckyEvent.CODEC);

    private static <T extends LuckyEvent> LuckyEventType<T> register(String name, MapCodec<T> codec) {
        return register(LuckyBlockMod.id(name), codec);
    }

    public static <T extends LuckyEvent> LuckyEventType<T> register(Identifier identifier, MapCodec<T> codec) {
        return Registry.register(LuckyBlockRegistries.LUCKY_EVENT_TYPE, identifier, new LuckyEventType<>(codec));
    }
}
