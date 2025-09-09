package fr.hugman.ultimate_lucky_block.api.lucky_event;

import com.mojang.serialization.MapCodec;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.AllOfSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.WeightedListSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.OneOfSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.lucky_event.selector.RepeatSelectorLuckyEvent;
import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistries;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Lucky event types registered by the Lucky Block mod.
 *
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyEventTypes {
    public static final LuckyEventType<SetBlockLuckyEvent> SET_BLOCK = register("set_block", SetBlockLuckyEvent.CODEC);
    public static final LuckyEventType<SummonEntityLuckyEvent> SUMMON_ENTITY = register("summon_entity", SummonEntityLuckyEvent.CODEC);
    public static final LuckyEventType<LootLuckyEvent> LOOT = register("loot", LootLuckyEvent.CODEC);

    public static final LuckyEventType<PillarLuckyEvent> WORLD_PILLAR = register("world_pillar", PillarLuckyEvent.CODEC);

    // SELECTORS
    public static final LuckyEventType<OneOfSelectorLuckyEvent> ONE_OF_SELECTOR = register("selector/one_of", OneOfSelectorLuckyEvent.CODEC);
    public static final LuckyEventType<AllOfSelectorLuckyEvent> ALL_OF_SELECTOR = register("selector/all_of", AllOfSelectorLuckyEvent.CODEC);
    public static final LuckyEventType<WeightedListSelectorLuckyEvent> WEIGHTED_LIST_SELECTOR = register("selector/weighted_list", WeightedListSelectorLuckyEvent.CODEC);
    public static final LuckyEventType<RepeatSelectorLuckyEvent> REPEAT_SELECTOR = register("selector/repeat", RepeatSelectorLuckyEvent.CODEC);

    private static <T extends LuckyEvent> LuckyEventType<T> register(String name, MapCodec<T> codec) {
        return register(UltimateLuckyBlock.id(name), codec);
    }

    public static <T extends LuckyEvent> LuckyEventType<T> register(Identifier identifier, MapCodec<T> codec) {
        return Registry.register(ULBRegistries.LUCKY_EVENT_TYPE, identifier, new LuckyEventType<>(codec));
    }
}
