package fr.hugman.uhc.api.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.UHC;
import fr.hugman.uhc.impl.game.phase.UHCWaiting;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;

public class UHCGameConfigs{
    public static RegistryKey<GameConfig<?>> of(String path) {
        return RegistryKey.of(GameConfigs.REGISTRY_KEY, UHC.id(path));
    }
}
