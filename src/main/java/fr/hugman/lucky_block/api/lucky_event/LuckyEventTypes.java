package fr.hugman.lucky_block.api.lucky_event;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistries;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LuckyEventTypes {
    public static final LuckyEventType<SummonEntityLuckyEvent> SUMMON_ENTITY = register("summon_entity", SummonEntityLuckyEvent.CODEC);
    public static final LuckyEventType<LootLuckyEvent> LOOT = register("loot", LootLuckyEvent.CODEC);

    private static <T extends LuckyEvent> LuckyEventType<T> register(String name, MapCodec<T> codec) {
        return register(LuckyBlockMod.id(name), codec);
    }

    public static <T extends LuckyEvent> LuckyEventType<T> register(Identifier identifier, MapCodec<T> codec) {
        return Registry.register(LuckyBlockRegistries.LUCKY_EVENT_TYPE, identifier, new LuckyEventType<>(codec));
    }
}
