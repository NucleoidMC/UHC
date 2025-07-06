package fr.hugman.lucky_block.api.lucky_event;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.registry.tag.TagKey;

public class LuckyEventTags {
    public static final TagKey<LuckyEvent> NORMAL = of("normal");
    public static final TagKey<LuckyEvent> LUCKY = of("lucky");
    public static final TagKey<LuckyEvent> UNLUCKY = of("unlucky");

    private static TagKey<LuckyEvent> of(String path) {
        return TagKey.of(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockMod.id(path));
    }
}
