package fr.hugman.lucky_block.block;

import fr.hugman.lucky_block.LuckyBlockMod;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class LuckyBlockKeys {
    public static final RegistryKey<Block> LUCKY_BLOCK = of("lucky_block");

    private static RegistryKey<Block> of(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, LuckyBlockMod.id(path));
    }
}
