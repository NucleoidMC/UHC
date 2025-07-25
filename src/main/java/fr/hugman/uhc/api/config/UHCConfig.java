package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UHCConfig {
    public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
            UHCTimersConfig.CODEC.optionalFieldOf("timers", UHCTimersConfig.DEFAULT).forGetter(UHCConfig::timersConfig),
            UHCModule.ENTRY_LIST_CODEC.optionalFieldOf("modules", RegistryEntryList.of()).forGetter(UHCConfig::modules)
    ).apply(instance, UHCConfig::new));

    public static final Codec<RegistryEntry<UHCConfig>> ENTRY_CODEC = RegistryElementCodec.of(UHCRegistryKeys.UHC_CONFIG, CODEC);

    private UHCMapConfig mapConfig;
    private UHCTimersConfig timersConfig;
    private RegistryEntryList<UHCModule> modules;

    public UHCConfig(UHCMapConfig mapConfig, UHCTimersConfig timersConfig, RegistryEntryList<UHCModule> modules) {
        this.mapConfig = mapConfig;
        this.timersConfig = timersConfig;
        this.modules = modules;
    }

    public UHCConfig(UHCMapConfig mapConfig) {
        this(mapConfig, UHCTimersConfig.DEFAULT, RegistryEntryList.of());
    }

    public UHCMapConfig mapConfig() {
        return mapConfig;
    }

    public UHCTimersConfig timersConfig() {
        return timersConfig;
    }

    public RegistryEntryList<UHCModule> modules() {
        return modules;
    }

    public void setModules(List<RegistryEntry<UHCModule>> modules) {
        this.modules = RegistryEntryList.of(modules);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UHCConfig) obj;
        return Objects.equals(this.mapConfig, that.mapConfig) &&
                Objects.equals(this.timersConfig, that.timersConfig) &&
                Objects.equals(this.modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapConfig, timersConfig, modules);
    }

    @Override
    public String toString() {
        return "UHCConfig[" +
                "mapConfig=" + mapConfig + ", " +
                "timersConfig=" + timersConfig + ", " +
                "modules=" + modules + ']';
    }

    @Override
    public UHCConfig clone() {
        return new UHCConfig(
                this.mapConfig.clone(),
                this.timersConfig.clone(),
                RegistryEntryList.of(new ArrayList<>(this.modules.stream().toList()))
        );
    }
}
