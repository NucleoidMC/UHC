package fr.hugman.lucky_block.lucky_event;

import fr.hugman.lucky_block.LuckyBlockMod;
import fr.hugman.lucky_block.registry.LuckyBlockRegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class LuckyEventTags {
    public static final TagKey<LuckyEvent> REGULAR = of("regular");

    private static TagKey<LuckyEvent> of(String path) {
        return TagKey.of(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockMod.id(path));
    }
}
