package com.hugman.uhc.config;

import com.hugman.uhc.module.Module;
import com.hugman.uhc.registry.UHCRegistryKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record UHCConfig(
        UHCMapConfig mapConfig,
        UHCTimersConfig chapterConfig,
        RegistryEntryList<Module> modules
) {
    public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
            UHCTimersConfig.CODEC.optionalFieldOf("chapters", UHCTimersConfig.DEFAULT).forGetter(UHCConfig::chapterConfig),
            Module.ENTRY_LIST_CODEC.optionalFieldOf("modules", RegistryEntryList.of()).forGetter(UHCConfig::modules)
    ).apply(instance, UHCConfig::new));

    public static final Codec<RegistryEntry<UHCConfig>> ENTRY_CODEC = RegistryElementCodec.of(UHCRegistryKeys.UHC_CONFIG, CODEC);
}
