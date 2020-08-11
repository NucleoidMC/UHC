package org.example.MODNAME.game;

import io.netty.util.internal.logging.AbstractInternalLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.PlayerRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.MODNAME.MODCLASS;
import org.example.MODNAME.game.map.MODCLASSMap;

import java.util.*;
import java.util.stream.Collectors;

public class MODCLASSActive {
    private final MODCLASSConfig config;

    public final GameWorld gameWorld;
    private final MODCLASSMap gameMap;

    // TODO replace with ServerPlayerEntity if players are removed upon leaving
    private final Object2ObjectMap<PlayerRef, MODCLASSPlayer> participants;
    private final MODCLASSSpawnLogic spawnLogic;
    private final MODCLASSIdle idle;
    private final boolean ignoreWinState;
    private final MODCLASSTimerBar timerBar;

    private MODCLASSActive(GameWorld gameWorld, MODCLASSMap map, MODCLASSConfig config, Set<PlayerRef> participants) {
        this.gameWorld = gameWorld;
        this.config = config;
        this.gameMap = map;
        this.spawnLogic = new MODCLASSSpawnLogic(gameWorld, map);
        this.participants = new Object2ObjectOpenHashMap<>();

        for (PlayerRef player : participants) {
            this.participants.put(player, new MODCLASSPlayer());
        }

        this.idle = new MODCLASSIdle();
        this.ignoreWinState = this.participants.size() <= 1;
        this.timerBar = new MODCLASSTimerBar();
    }

    public static void open(GameWorld gameWorld, MODCLASSMap map, MODCLASSConfig config) {
        Set<PlayerRef> participants = gameWorld.getPlayers().stream()
                .map(PlayerRef::of)
                .collect(Collectors.toSet());
        MODCLASSActive active = new MODCLASSActive(gameWorld, map, config, participants);

        gameWorld.newGame(builder -> {
            builder.setRule(GameRule.ALLOW_CRAFTING, RuleResult.DENY);
            builder.setRule(GameRule.ALLOW_PORTALS, RuleResult.DENY);
            builder.setRule(GameRule.ALLOW_PVP, RuleResult.DENY);
            builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
            builder.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            builder.setRule(GameRule.ENABLE_HUNGER, RuleResult.DENY);

            builder.on(GameOpenListener.EVENT, active::onOpen);
            builder.on(GameCloseListener.EVENT, active::onClose);

            builder.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            builder.on(PlayerAddListener.EVENT, active::addPlayer);

            builder.on(GameTickListener.EVENT, active::tick);

            builder.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            builder.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
        });
    }

    private void onOpen() {
        ServerWorld world = this.gameWorld.getWorld();
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(world, this::spawnParticipant);
        }
        this.idle.onOpen(world.getTime(), this.config);
        // TODO setup logic
    }

    private void onClose() {
        this.timerBar.close();
        // TODO teardown logic
    }

    private void addPlayer(ServerPlayerEntity player) {
        if (!this.participants.containsKey(PlayerRef.of(player))) {
            this.spawnSpectator(player);
        }
        this.timerBar.addPlayer(player);
    }

    private boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        // TODO handle damage
        this.spawnParticipant(player);
        return true;
    }

    private boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        // TODO handle death
        this.spawnParticipant(player);
        return true;
    }

    private void spawnParticipant(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }

    private void spawnSpectator(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void tick() {
        ServerWorld world = this.gameWorld.getWorld();
        long time = world.getTime();

        MODCLASSIdle.IdleTickResult result = this.idle.tick(time, gameWorld);

        switch (result) {
            case CONTINUE_TICK:
                break;
            case TICK_FINISHED:
                return;
            case GAME_FINISHED:
                this.broadcastWin(this.checkWinResult());
                return;
            case GAME_CLOSED:
                this.gameWorld.closeWorld();
                return;
        }

        this.timerBar.update(this.idle.finishTime - time, this.config.timeLimitSecs * 20);

        // TODO tick logic
    }

    protected static void broadcastMessage(Text message, GameWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        };
    }

    protected static void broadcastSound(SoundEvent sound, float pitch, GameWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.playSound(sound, SoundCategory.PLAYERS, 1.0F, pitch);
        };
    }

    protected static void broadcastSound(SoundEvent sound,  GameWorld world) {
        broadcastSound(sound, 1.0f, world);
    }

    protected static void broadcastTitle(Text message, GameWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            TitleS2CPacket packet = new TitleS2CPacket(TitleS2CPacket.Action.TITLE, message, 1, 5,  3);
            player.networkHandler.sendPacket(packet);
        }
    }

    private void broadcastWin(WinResult result) {
        ServerPlayerEntity winningPlayer = result.getWinningPlayer();

        Text message;
        if (winningPlayer != null) {
            message = winningPlayer.getDisplayName().shallowCopy().append(" has won the game!").formatted(Formatting.GOLD);
        } else {
            message = new LiteralText("The game ended, but nobody won!").formatted(Formatting.GOLD);
        }

        broadcastMessage(message, this.gameWorld);
        broadcastSound(SoundEvents.ENTITY_VILLAGER_YES, this.gameWorld);
    }

    private WinResult checkWinResult() {
        // for testing purposes: don't end the game if we only ever had one participant
        if (this.ignoreWinState) {
            return WinResult.no();
        }

        ServerWorld world = this.gameWorld.getWorld();
        ServerPlayerEntity winningPlayer = null;

        // TODO win result logic
        return WinResult.no();
    }

    static class WinResult {
        final ServerPlayerEntity winningPlayer;
        final boolean win;

        private WinResult(ServerPlayerEntity winningPlayer, boolean win) {
            this.winningPlayer = winningPlayer;
            this.win = win;
        }

        static WinResult no() {
            return new WinResult(null, false);
        }

        static WinResult win(ServerPlayerEntity player) {
            return new WinResult(player, true);
        }

        public boolean isWin() {
            return this.win;
        }

        public ServerPlayerEntity getWinningPlayer() {
            return this.winningPlayer;
        }
    }
}
