package fr.hugman.lucky_block.api.lucky_event;

import fr.hugman.lucky_block.api.registry.LuckyBlockRegistryKeys;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
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
        return RegistryKey.of(LuckyBlockRegistryKeys.LUCKY_EVENT, LuckyBlockMod.id("pool/" + path));
    }
}
