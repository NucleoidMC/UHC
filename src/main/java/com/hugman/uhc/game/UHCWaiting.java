package com.hugman.uhc.game;

import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.event.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import com.hugman.uhc.game.map.UHCMap;
import com.hugman.uhc.game.map.UHCMapGenerator;
import xyz.nucleoid.fantasy.BubbleWorldConfig;

public class UHCWaiting {
    private final GameSpace gameSpace;
    private final UHCMap map;
    private final UHCConfig config;
    private final UHCSpawnLogic spawnLogic;

    private UHCWaiting(GameSpace gameSpace, UHCMap map, UHCConfig config) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.spawnLogic = new UHCSpawnLogic(gameSpace, map);
    }

    public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
        UHCConfig config = context.getConfig();
        UHCMapGenerator generator = new UHCMapGenerator(config.mapConfig);
        UHCMap map = generator.build();

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameMode.SPECTATOR);

        return context.createOpenProcedure(worldConfig, game -> {
            UHCWaiting waiting = new UHCWaiting(game.getSpace(), map, context.getConfig());

            GameWaitingLobby.applyTo(game, config.playerConfig);

            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
        });
    }

    private StartResult requestStart() {
        UHCActive.open(this.gameSpace, this.map, this.config);
        return StartResult.OK;
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(20.0f);
        this.spawnPlayer(player);
        return ActionResult.FAIL;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }
}
