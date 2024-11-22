package com.hugman.lucky_block;

import com.hugman.lucky_block.block.LuckyBlocks;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class LuckyBlockMod implements ModInitializer {
    private static final String MOD_ID = "lucky_block";

    @Override
    public void onInitialize() {
        LuckyBlocks.register();
        PolymerResourcePackUtils.addModAssets(MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
