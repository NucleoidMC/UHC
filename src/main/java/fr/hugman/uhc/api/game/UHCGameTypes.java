package fr.hugman.uhc.api.game;

import com.mojang.serialization.MapCodec;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.impl.game.phase.UHCWaiting;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.GameTypes;

public class UHCGameTypes {
    public static final GameType<UHCGameConfig> STANDARD = of("standard", UHCGameConfig.CODEC, UHCWaiting::open);

    public static <C> GameType<C> of(String path, MapCodec<C> configCodec, GameType.Open<C> open) {
        return GameTypes.register(UHC.id(path), configCodec, open);
    }
}
