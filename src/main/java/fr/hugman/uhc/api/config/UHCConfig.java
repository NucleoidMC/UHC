package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.module.Module;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
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

    public static UHCConfig of(UHCMapConfig mapConfig) {
        return new UHCConfig(
                mapConfig,
                UHCTimersConfig.DEFAULT,
                RegistryEntryList.of()
        );
    }
}
