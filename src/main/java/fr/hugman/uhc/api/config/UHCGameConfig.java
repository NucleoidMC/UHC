package fr.hugman.uhc.api.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record UHCGameConfig(
        WaitingLobbyConfig players,
        int teamSize,
        Holder<UHCConfig> uhcConfig
) {
    public static final MapCodec<UHCGameConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(UHCGameConfig::players),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("team_size", 1).forGetter(UHCGameConfig::teamSize),
            UHCConfig.ENTRY_CODEC.fieldOf("config").forGetter(UHCGameConfig::uhcConfig)
    ).apply(instance, UHCGameConfig::new));

    public UHCGameConfig(WaitingLobbyConfig players, Holder<UHCConfig> config) {
        this(players, 1, config);
    }
}
