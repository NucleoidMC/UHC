package fr.hugman.ultimate_lucky_block.api.lucky_event;

import fr.hugman.ultimate_lucky_block.api.registry.ULBRegistryKeys;
import fr.hugman.ultimate_lucky_block.impl.UltimateLuckyBlock;
import net.minecraft.registry.RegistryKey;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyPoolEvents {
    public static final RegistryKey<LuckyEvent> NORMAL = of("normal");
    public static final RegistryKey<LuckyEvent> LUCKY = of("lucky");
    public static final RegistryKey<LuckyEvent> VERY_LUCKY = of("very_lucky");
    public static final RegistryKey<LuckyEvent> UNLUCKY = of("unlucky");
    public static final RegistryKey<LuckyEvent> VERY_UNLUCKY = of("very_unlucky");
    public static final RegistryKey<LuckyEvent> DOUBLE = of("double");
    public static final RegistryKey<LuckyEvent> TRIPLE = of("triple");

    private static RegistryKey<LuckyEvent> of(String path) {
        return RegistryKey.of(ULBRegistryKeys.LUCKY_EVENT, UltimateLuckyBlock.id("pool/" + path));
    }
}
