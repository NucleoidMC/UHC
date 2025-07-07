package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.MapCodec;
import fr.hugman.lucky_block.api.lucky_event.selector.AllOfSelectorLuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.selector.LuckSelectorLuckyEvent;
import fr.hugman.lucky_block.api.lucky_event.selector.OneOfSelectorLuckyEvent;
import fr.hugman.lucky_block.api.registry.LuckyBlockRegistries;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LuckyEventTypes {
    public static final LuckyEventType<SummonEntityLuckyEvent> SUMMON_ENTITY = register("summon_entity", SummonEntityLuckyEvent.CODEC);
    public static final LuckyEventType<LootLuckyEvent> LOOT = register("loot", LootLuckyEvent.CODEC);

    // SELECTORS
    public static final LuckyEventType<OneOfSelectorLuckyEvent> ONE_OF_SELECTOR = register("selector/one_of", OneOfSelectorLuckyEvent.CODEC);
    public static final LuckyEventType<AllOfSelectorLuckyEvent> ALL_OF_SELECTOR = register("selector/all_of", AllOfSelectorLuckyEvent.CODEC);
    public static final LuckyEventType<LuckSelectorLuckyEvent> LUCK_SELECTOR = register("selector/luck", LuckSelectorLuckyEvent.CODEC);

    private static <T extends LuckyEvent> LuckyEventType<T> register(String name, MapCodec<T> codec) {
        return register(LuckyBlockMod.id(name), codec);
    }

    public static <T extends LuckyEvent> LuckyEventType<T> register(Identifier identifier, MapCodec<T> codec) {
        return Registry.register(LuckyBlockRegistries.LUCKY_EVENT_TYPE, identifier, new LuckyEventType<>(codec));
    }
}
