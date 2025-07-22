package fr.hugman.lucky_block.api.block;

import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockKeys {
    public static final RegistryKey<Block> LUCKY_BLOCK = of("lucky_block");

    public static final RegistryKey<Block> SUPER_LUCKY_BLOCK = of("super_lucky_block");
    public static final RegistryKey<Block> VERY_LUCKY_BLOCK = of("very_lucky_block");
    public static final RegistryKey<Block> UNLUCKY_BLOCK = of("unlucky_block");
    public static final RegistryKey<Block> VERY_UNLUCKY_BLOCK = of("very_unlucky_block");

    public static final RegistryKey<Block> DOUBLE_LUCKY_BLOCK = of("double_lucky_block");
    public static final RegistryKey<Block> TRIPLE_LUCKY_BLOCK = of("triple_lucky_block");

    private static RegistryKey<Block> of(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, LuckyBlockMod.id(path));
    }
}
