package fr.hugman.uhc.api.portal.menu;

import com.mojang.serialization.MapCodec;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntry;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntryConfig;

public record UHCCreatorMenuEntryConfig() implements MenuEntryConfig {
    public static final MapCodec<UHCCreatorMenuEntryConfig> CODEC = MapCodec.unit(UHCCreatorMenuEntryConfig::new);

    @Override
    public MenuEntry createEntry() {
        return new UHCCreatorMenuEntry();
    }

    @Override
    public MapCodec<UHCCreatorMenuEntryConfig> codec() {
        return CODEC;
    }
}
