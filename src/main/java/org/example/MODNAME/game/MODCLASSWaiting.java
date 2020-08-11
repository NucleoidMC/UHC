package org.example.MODNAME.game;

import net.gegy1000.plasmid.game.GameWorld;
import net.gegy1000.plasmid.game.GameWorldState;
import net.gegy1000.plasmid.game.StartResult;
import net.gegy1000.plasmid.game.config.PlayerConfig;
import net.gegy1000.plasmid.game.event.OfferPlayerListener;
import net.gegy1000.plasmid.game.event.PlayerAddListener;
import net.gegy1000.plasmid.game.event.PlayerDeathListener;
import net.gegy1000.plasmid.game.event.RequestStartListener;
import net.gegy1000.plasmid.game.player.JoinResult;
import net.gegy1000.plasmid.game.rule.GameRule;
import net.gegy1000.plasmid.game.rule.RuleResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.example.MODNAME.game.map.MODCLASSMap;
import org.example.MODNAME.game.map.MODCLASSMapGenerator;

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

    public static CompletableFuture<Void> open(GameWorldState worldState, MODCLASSConfig config) {
        MODCLASSMapGenerator generator = new MODCLASSMapGenerator(config.mapConfig);

        return generator.create().thenAccept(map -> {
            GameWorld gameWorld = worldState.openWorld(map.asGenerator());

            MODCLASSWaiting waiting = new MODCLASSWaiting(gameWorld, map, config);

            gameWorld.newGame(builder -> {
                builder.setRule(GameRule.ALLOW_CRAFTING, RuleResult.DENY);
                builder.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
                builder.setRule(GameRule.ALLOW_PVP, RuleResult.DENY);
                builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
                builder.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);
                builder.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);

                builder.on(RequestStartListener.EVENT, waiting::requestStart);
                builder.on(OfferPlayerListener.EVENT, waiting::offerPlayer);


                builder.on(PlayerAddListener.EVENT, waiting::addPlayer);
                builder.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
            });
        });
    }

    private JoinResult offerPlayer(ServerPlayerEntity player) {
        if (this.gameWorld.getPlayerCount() >= this.config.playerConfig.getMaxPlayers()) {
            return JoinResult.gameFull();
        }

        return JoinResult.ok();
    }

    private StartResult requestStart() {
        PlayerConfig playerConfig = this.config.playerConfig;
        if (this.gameWorld.getPlayerCount() < playerConfig.getMinPlayers()) {
            return StartResult.notEnoughPlayers();
        }

        MODCLASSActive.open(this.gameWorld, this.map, this.config);

        return StartResult.ok();
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);
    }

    private boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        this.spawnPlayer(player);
        return true;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }
}
