package fr.hugman.uhc.api.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UHCConfig {
    public static final Codec<UHCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlasmidCodecs.TEXT.optionalFieldOf("name").forGetter(UHCConfig::name),
            ItemStackTemplate.CODEC.optionalFieldOf("icon").forGetter(UHCConfig::icon),
            UHCMapConfig.CODEC.fieldOf("map").forGetter(UHCConfig::mapConfig),
            UHCTimersConfig.CODEC.optionalFieldOf("timers", UHCTimersConfig.DEFAULT).forGetter(UHCConfig::timersConfig),
            UHCModule.ENTRY_LIST_CODEC.optionalFieldOf("modules", HolderSet.direct()).forGetter(UHCConfig::modules)
    ).apply(instance, UHCConfig::new));

    public static final Codec<Holder<UHCConfig>> ENTRY_CODEC = RegistryFileCodec.create(UHCRegistryKeys.UHC_CONFIG, CODEC);

    private Optional<Component> name;
    private Optional<ItemStackTemplate> icon;
    private UHCMapConfig mapConfig;
    private UHCTimersConfig timersConfig;
    private HolderSet<UHCModule> modules;

    public UHCConfig(Optional<Component> name, Optional<ItemStackTemplate> icon, UHCMapConfig mapConfig, UHCTimersConfig timersConfig, HolderSet<UHCModule> modules) {
        this.name = name;
        this.icon = icon;
        this.mapConfig = mapConfig;
        this.timersConfig = timersConfig;
        this.modules = modules;
    }

    public UHCConfig(UHCMapConfig mapConfig, UHCTimersConfig timersConfig, HolderSet<UHCModule> modules) {
        this(Optional.empty(), Optional.empty(), mapConfig, timersConfig, modules);
    }

    public UHCConfig(UHCMapConfig mapConfig) {
        this(mapConfig, UHCTimersConfig.DEFAULT, HolderSet.direct());
    }

    /**
     * Names a config and gives it an icon, so that it can be offered as a preset in the creator.
     */
    public UHCConfig display(Component name, ItemLike icon) {
        this.name = Optional.of(name);
        this.icon = Optional.of(new ItemStackTemplate(icon.asItem()));
        return this;
    }

    public Optional<Component> name() {
        return name;
    }

    public Optional<ItemStackTemplate> icon() {
        return icon;
    }

    public UHCMapConfig mapConfig() {
        return mapConfig;
    }

    public void setMapConfig(UHCMapConfig mapConfig) {
        this.mapConfig = mapConfig;
    }

    public UHCTimersConfig timersConfig() {
        return timersConfig;
    }

    public void setTimersConfig(UHCTimersConfig timersConfig) {
        this.timersConfig = timersConfig;
    }

    public HolderSet<UHCModule> modules() {
        return modules;
    }

    public void setModules(List<Holder<UHCModule>> modules) {
        this.modules = HolderSet.direct(modules);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UHCConfig) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.icon, that.icon) &&
                Objects.equals(this.mapConfig, that.mapConfig) &&
                Objects.equals(this.timersConfig, that.timersConfig) &&
                Objects.equals(this.modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, icon, mapConfig, timersConfig, modules);
    }

    @Override
    public String toString() {
        return "UHCConfig[" +
                "name=" + name + ", " +
                "icon=" + icon + ", " +
                "mapConfig=" + mapConfig + ", " +
                "timersConfig=" + timersConfig + ", " +
                "modules=" + modules + ']';
    }

    @Override
    public UHCConfig clone() {
        return new UHCConfig(
                this.name,
                this.icon,
                this.mapConfig.clone(),
                this.timersConfig.clone(),
                HolderSet.direct(new ArrayList<>(this.modules.stream().toList()))
        );
    }
}
