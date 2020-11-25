package org.example.MODNAME.game;

import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.event.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.example.MODNAME.game.map.MODCLASSMap;
import org.example.MODNAME.game.map.MODCLASSMapGenerator;
import xyz.nucleoid.fantasy.BubbleWorldConfig;

public class MODCLASSWaiting {
    private final GameSpace gameSpace;
    private final MODCLASSMap map;
    private final MODCLASSConfig config;
    private final MODCLASSSpawnLogic spawnLogic;

    private MODCLASSWaiting(GameSpace gameSpace, MODCLASSMap map, MODCLASSConfig config) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.spawnLogic = new MODCLASSSpawnLogic(gameSpace, map);
    }

    public static GameOpenProcedure open(GameOpenContext<MODCLASSConfig> context) {
        MODCLASSConfig config = context.getConfig();
        MODCLASSMapGenerator generator = new MODCLASSMapGenerator(config.mapConfig);
        MODCLASSMap map = generator.build();

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameMode.SPECTATOR);

        return context.createOpenProcedure(worldConfig, game -> {
            MODCLASSWaiting waiting = new MODCLASSWaiting(game.getSpace(), map, context.getConfig());

            GameWaitingLobby.applyTo(game, config.playerConfig);

            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
        });
    }

    private StartResult requestStart() {
        MODCLASSActive.open(this.gameSpace, this.map, this.config);
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
