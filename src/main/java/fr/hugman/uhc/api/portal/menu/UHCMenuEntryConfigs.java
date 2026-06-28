package fr.hugman.uhc.api.portal.menu;

import com.mojang.serialization.MapCodec;
import fr.hugman.uhc.impl.UHC;
import xyz.nucleoid.plasmid.api.portal.menu.MenuEntryConfigs;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntryConfig;

public class UHCMenuEntryConfigs {
    public static MapCodec<? extends MenuEntryConfig> UHC_CREATOR = register("uhc_creator", UHCCreatorMenuEntryConfig.CODEC);

    private static MapCodec<? extends MenuEntryConfig> register(String key, MapCodec<? extends MenuEntryConfig> codec) {
        return MenuEntryConfigs.register(UHC.id(key), codec);
    }
}
