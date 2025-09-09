package fr.hugman.ultimate_lucky_block.api.lucky_event;

import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.tag.TagKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyEventTags {
    public static final TagKey<LuckyEvent> VERY_UNLUCKY = of("very_unlucky");
    public static final TagKey<LuckyEvent> UNLUCKY = of("unlucky");
    public static final TagKey<LuckyEvent> NORMAL = of("normal");
    public static final TagKey<LuckyEvent> LUCKY = of("lucky");
    public static final TagKey<LuckyEvent> VERY_LUCKY = of("very_lucky");

    private static TagKey<LuckyEvent> of(String path) {
        return TagKey.of(ULBRegistryKeys.LUCKY_EVENT, UltimateLuckyBlock.id(path));
    }
}
