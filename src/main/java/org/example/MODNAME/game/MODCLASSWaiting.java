package org.example.MODNAME.game;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.example.MODNAME.game.map.MODCLASSMap;
import org.example.MODNAME.game.map.MODCLASSMapGenerator;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldConfig;

import java.util.concurrent.CompletableFuture;

public class MODCLASSWaiting {
    private final GameWorld gameWorld;
    private final MODCLASSMap map;
    private final MODCLASSConfig config;
    private final MODCLASSSpawnLogic spawnLogic;

    private MODCLASSWaiting(GameWorld gameWorld, MODCLASSMap map, MODCLASSConfig config) {
        this.gameWorld = gameWorld;
        this.map = map;
        this.config = config;
        this.spawnLogic = new MODCLASSSpawnLogic(gameWorld, map);
    }

    public static CompletableFuture<GameWorld> open(GameOpenContext<MODCLASSConfig> context) {
        MODCLASSMapGenerator generator = new MODCLASSMapGenerator(context.getConfig().mapConfig);

        return generator.create().thenCompose(map -> {
            BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                    .setGenerator(map.asGenerator(context.getServer()))
                    .setDefaultGameMode(GameMode.SPECTATOR);

            return context.openWorld(worldConfig).thenApply(gameWorld -> {
                MODCLASSWaiting waiting = new MODCLASSWaiting(gameWorld, map, context.getConfig());

                GameWaitingLobby.open(gameWorld, context.getConfig().playerConfig, builder -> {
                    builder.on(RequestStartListener.EVENT, waiting::requestStart);
                    builder.on(PlayerAddListener.EVENT, waiting::addPlayer);
                    builder.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
                });

                return gameWorld;
            });
        });
    }

    private StartResult requestStart() {
        MODCLASSActive.open(this.gameWorld, this.map, this.config);
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
