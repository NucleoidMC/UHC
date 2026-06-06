package fr.hugman.uhc.impl.game.phase;

import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.modifier.ModifierType;
import fr.hugman.uhc.api.modifier.PermanentEffectModifier;
import fr.hugman.uhc.api.modifier.PlayerAttributeModifier;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.module.UHCModuleEvents;
import fr.hugman.uhc.api.util.Messenger;
import fr.hugman.uhc.api.util.TickUtil;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.impl.game.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.plasmid.api.game.GameActivity;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.player.PlayerSet;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.Optional;

public class UHCActive {
    private final GameSpace gameSpace;
    private final ServerLevel level;
    private final GameActivity activity;
    private final int spawnOffset;

    private final UHCPlayerManager playerManager;
    private final UHCTimers timers;
    private final UHCSpawner spawnLogic;
    private final UHCBar bar;
    private final UHCSideBar sideBar;
    private final ModuleManager moduleManager;
    private final Messenger msg;

    private long gameStartTick;
    private long startInvulnerableTick;
    private long startWarmupTick;
    private long finaleCagesTick;
    private long finaleInvulnerabilityTick;
    private long reducingTick;
    private long deathMatchTick;
    private long gameEndTick;
    private long gameCloseTick;

    private boolean invulnerable;
    private boolean isFinished = false;

    private UHCActive(
            GameSpace gameSpace,
            ServerLevel level,
            GameActivity activity,
            int spawnOffset,
            UHCPlayerManager playerManager,
            UHCTimers timers,
            UHCSpawner spawnLogic,
            UHCBar bar,
            UHCSideBar sideBar,
            ModuleManager moduleManager,
            Messenger msg
    ) {
        this.gameSpace = gameSpace;
        this.level = level;
        this.activity = activity;
        this.spawnOffset = spawnOffset;
        this.playerManager = playerManager;
        this.timers = timers;
        this.spawnLogic = spawnLogic;
        this.bar = bar;
        this.sideBar = sideBar;
        this.moduleManager = moduleManager;
        this.msg = msg;
    }

    private static UHCActive of(
            GameActivity activity,
            GameSpace gameSpace,
            ServerLevel level,
            UHCGameConfig config
    ) {
        var moduleManager = gameSpace.getAttachment(ModuleManager.ATTACHMENT);
        assert moduleManager != null;
        var playerManager = UHCPlayerManager.of(activity, gameSpace, config);
        var widgets = GlobalWidgets.addTo(activity);
        var messenger = new Messenger(gameSpace.getPlayers());
        return new UHCActive(
                gameSpace,
                level,
                activity,
                config.uhcConfig().value().mapConfig().spawnOffset(),
                playerManager,
                new UHCTimers(config, playerManager.count()),
                new UHCSpawner(level),
                UHCBar.create(widgets, gameSpace, messenger),
                UHCSideBar.create(widgets, gameSpace),
                moduleManager,
                messenger
        );
    }

    public static void start(GameSpace gameSpace, ServerLevel level, UHCGameConfig config) {
        gameSpace.setActivity(activity -> {
            UHCActive active = UHCActive.of(activity, gameSpace, level, config);

            activity.allow(GameRuleType.CRAFTING);
            activity.deny(GameRuleType.PORTALS);
            activity.deny(GameRuleType.PVP);
            activity.allow(GameRuleType.BLOCK_DROPS);
            activity.allow(GameRuleType.FALL_DAMAGE);
            activity.allow(GameRuleType.HUNGER);

            activity.listen(GameActivityEvents.ENABLE, active::enable);

            activity.listen(GamePlayerEvents.OFFER, JoinOffer::acceptSpectators);
            activity.listen(GamePlayerEvents.ACCEPT, active::acceptPlayer);
            activity.listen(GamePlayerEvents.LEAVE, active::playerLeave);

            activity.listen(GameActivityEvents.TICK, active::tick);
            activity.listen(UHCModuleEvents.ENABLE, active::enableModule);
            activity.listen(UHCModuleEvents.DISABLE, active::disableModule);

            activity.listen(PlayerDamageEvent.EVENT, active::onPlayerDamage);
            activity.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);

            active.moduleManager.setupListeners(activity, active.playerManager);
        });
    }

    // GENERAL GAME MANAGEMENT
    private void enable() {
        // Setup
        this.level.getWorldBorder().setCenter(0, 0);
        this.level.getWorldBorder().setSize(this.timers.getStartMapSize());
        this.level.getWorldBorder().setDamagePerBlock(0.5);
        this.gameSpace.getPlayers().forEach(player -> player.connection.send(new ClientboundInitializeBorderPacket(this.level.getWorldBorder())));

        this.gameStartTick = this.level.getGameTime();
        this.startInvulnerableTick = this.level.getGameTime() + this.timers.getInCagesTime();
        this.startWarmupTick = this.startInvulnerableTick + this.timers.getInvulnerabilityTime();
        this.finaleCagesTick = this.startWarmupTick + this.timers.getWarmupTime();
        this.finaleInvulnerabilityTick = this.finaleCagesTick + this.timers.getInCagesTime();
        this.reducingTick = this.finaleInvulnerabilityTick + this.timers.getInvulnerabilityTime();
        this.deathMatchTick = this.reducingTick + this.timers.getShrinkingTime();
        this.gameEndTick = this.deathMatchTick + this.timers.getDeathmatchTime();
        this.gameCloseTick = this.gameEndTick + 600;

        // Start - Cage chapter
        this.playerManager.forEachAlive(player -> {
            this.resetPlayer(player);
            this.refreshPlayerAttributes(player);
            player.setGameMode(GameType.ADVENTURE);
        });
        this.tpToCages();
        this.bar.set("text.uhc.dropping", this.timers.getInCagesTime(), this.startInvulnerableTick, BossEvent.BossBarColor.PURPLE);
    }

    private void tick() {
        long gameTime = this.level.getGameTime();

        this.bar.tick(this.level);
        this.sideBar.update(gameTime - this.gameStartTick, (int) this.level.getWorldBorder().getSize(), this.playerManager);

        // Game ends
        if (isFinished) {
            if (gameTime > this.gameCloseTick) {
                this.gameSpace.close(GameCloseReason.FINISHED);
            }
            return;
        }

        // Start - Cage chapter (@ 80%)
        if (gameTime == this.startInvulnerableTick - (timers.getInCagesTime() * 0.8)) {
            msg.moduleList(this.moduleManager);
        }
        // Start - Invulnerable chapter
        else if (gameTime == this.startInvulnerableTick) {
            this.dropCages();
            msg.info("text.uhc.dropped_players");
            msg.info("text.uhc.world_will_shrink", TickUtil.formatPretty(this.finaleCagesTick - gameTime));

            this.bar.set(Messenger.SYMBOL_SHIELD, "text.uhc.vulnerable", this.timers.getInvulnerabilityTime(), this.startWarmupTick, BossEvent.BossBarColor.YELLOW);
        }

        // Start - Warmup chapter
        else if (gameTime == this.startWarmupTick) {
            this.setInvulnerable(false);
            msg.danger(Messenger.SYMBOL_SHIELD, "text.uhc.no_longer_immune");

            this.bar.set("text.uhc.tp", this.timers.getWarmupTime(), this.finaleCagesTick, BossEvent.BossBarColor.BLUE);
        }

        // Finale - Cages chapter
        else if (gameTime == this.finaleCagesTick) {
            this.playerManager.forEachAlive(player -> {
                this.clearPlayer(player);
                this.refreshPlayerAttributes(player);
                player.setGameMode(GameType.ADVENTURE);
            });
            this.tpToCages();
            msg.info("text.uhc.shrinking_when_pvp");

            this.bar.set("text.uhc.dropping", this.timers.getInCagesTime(), this.finaleInvulnerabilityTick, BossEvent.BossBarColor.PURPLE);
        }

        // Finale - Invulnerability chapter
        else if (gameTime == this.finaleInvulnerabilityTick) {
            this.dropCages();
            msg.info("text.uhc.dropped_players");

            this.bar.set(Messenger.SYMBOL_SWORD, "text.uhc.pvp", this.timers.getInvulnerabilityTime(), this.reducingTick, BossEvent.BossBarColor.YELLOW);
        }

        // Finale - Reducing chapter
        else if (gameTime == this.reducingTick) {
            this.setInvulnerable(false);
            msg.danger(Messenger.SYMBOL_SHIELD, "text.uhc.no_longer_immune");

            this.setPvp(true);
            msg.danger(Messenger.SYMBOL_SKULL, "text.uhc.pvp_enabled");

            this.level.getWorldBorder().lerpSizeBetween(this.timers.getStartMapSize(), this.timers.getEndMapSize(), this.timers.getShrinkingTime() * 50L, this.level.getGameTime());
            this.gameSpace.getPlayers().forEach(player -> player.connection.send(new ClientboundSetBorderLerpSizePacket(this.level.getWorldBorder())));
            msg.danger("text.uhc.shrinking_start");

            this.bar.set("text.uhc.shrinking_finish", this.timers.getShrinkingTime(), this.deathMatchTick, BossEvent.BossBarColor.RED);
        }

        // Finale - Deathmatch chapter
        else if (gameTime == this.deathMatchTick) {
            this.bar.setFull(Component.literal("🗡").append(Component.translatable("text.uhc.deathmatchTime")).append("🗡"));
            this.level.getWorldBorder().setDamagePerBlock(2.5);
            this.level.getWorldBorder().setSafeZone(0.125);
            msg.info(Messenger.SYMBOL_SWORD, "text.uhc.last_one_wins");
            this.checkForWinner();
        }
    }

    // GENERAL PLAYER MANAGEMENT
    private JoinAcceptorResult acceptPlayer(JoinAcceptor joinAcceptor) {
        return joinAcceptor
                .teleport(this.level, UHCSpawner.getSurfaceBlock(level, 0, 0))
                .thenRunForEach(player -> {
                    player.setGameMode(GameType.SPECTATOR);
                    player.connection.send(new ClientboundInitializeBorderPacket(this.level.getWorldBorder()));
                });
    }

    private void playerLeave(ServerPlayer player) {
        if (playerManager.contains(player)) {
            if (!playerManager.get(player).isEliminated()) {
                msg.elimination(player);
                this.eliminateParticipant(player);
            }
        }
    }

    private void eliminateParticipant(ServerPlayer player) {
        Containers.dropContents(player.level(), player.blockPosition(), player.getInventory());
        player.setGameMode(GameType.SPECTATOR);
        this.resetPlayer(player);
        this.spawnLogic.spawnPlayerAtCenter(player);
        playerManager.get(player).eliminate();
        this.checkForWinner();
    }

    public void resetPlayer(ServerPlayer player) {
        this.clearPlayer(player);
        player.getInventory().clearContent();
        player.getEnderChestInventory().clearContent();
        player.removeAllEffects();
        player.getFoodData().setFoodLevel(20);
        player.setExperienceLevels(0);
        player.setExperiencePoints(0);
        player.setHealth(player.getMaxHealth());
    }

    public void clearPlayer(ServerPlayer player) {
        player.clearFire();
        player.fallDistance = 0.0F;
    }

    public void refreshPlayerAttributes(ServerPlayer player) {
        for (PlayerAttributeModifier piece : this.moduleManager.modifiers(ModifierType.PLAYER_ATTRIBUTE)) {
            piece.refreshAttribute(player);
        }
        player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), player.getAttributes().getAttributesToSync()));
    }

    public void applyPlayerEffects(ServerPlayer player) {
        for (PermanentEffectModifier piece : this.moduleManager.modifiers(ModifierType.PERMANENT_EFFECT)) {
            piece.setEffect(player);
        }
    }

    private void checkForWinner() {
        PlayerSet players = this.gameSpace.getPlayers();

        // Remove empty teams
        this.playerManager.refreshAliveTeams();
        // Only one team is left, so they win
        if (this.playerManager.aliveTeamsCount() <= 1) {
            if (this.playerManager.allTeamsEmpty()) {
                players.sendMessage(Component.literal("\n").append(Component.translatable("text.uhc.none_win").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)).append("\n"));
                UHC.LOGGER.warn("There are no teams left! Consider reviewing the minimum amount of players needed to start a game, so that there are at least 2 teams in the game.");
            } else {
                GameTeam lastTeam = this.playerManager.getLastTeam();
                PlayerSet teamMembers = this.playerManager.teamPlayers(lastTeam.key());
                if (teamMembers.size() <= 0) {
                    players.sendMessage(Component.literal("\n").append(Component.translatable("text.uhc.none_win").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)).append("\n"));
                    UHC.LOGGER.warn("There is only one team left, but there are no players in it!");
                } else if (teamMembers.size() == 1) {
                    Optional<ServerPlayer> participant = teamMembers.stream().findFirst();
                    participant.ifPresent(playerEntity -> players.sendMessage(Component.literal("\n").append(Component.translatable("text.uhc.player_win.solo", playerEntity.getName()).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)).append("\n")));
                } else {
                    players.sendMessage(Component.literal("\n").append(Component.translatable("text.uhc.player_win.team", ComponentUtils.formatList(teamMembers.stream().toList(), Player::getName)).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD)).append("\n"));
                }
                teamMembers.forEach(playerEntity -> playerEntity.setGameMode(GameType.ADVENTURE));
                this.setInvulnerable(true);
                this.setPvp(false);
            }
            players.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
            this.gameCloseTick = this.level.getGameTime() + 200;
            this.bar.close();
            this.isFinished = true;
            this.playerManager.clear();
        }
    }

    // GAME STATES
    private void setInvulnerable(boolean b) {
        this.invulnerable = b;
        this.activity.setRule(GameRuleType.HUNGER, b ? EventResult.DENY : EventResult.ALLOW);
        //TODO check modules
        this.activity.setRule(GameRuleType.FALL_DAMAGE, b ? EventResult.DENY : EventResult.ALLOW);
    }

    private void setPvp(boolean b) {
        this.activity.setRule(GameRuleType.PVP, b ? EventResult.ALLOW : EventResult.DENY);
    }

    private void setInteractWithLevel(boolean b) {
        this.activity.setRule(GameRuleType.BREAK_BLOCKS, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.PLACE_BLOCKS, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.INTERACTION, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.CRAFTING, b ? EventResult.ALLOW : EventResult.DENY);
    }

    private void tpToCages() {
        this.setInvulnerable(true);
        this.setInteractWithLevel(false);

        int index = 0;
        for (GameTeam team : this.playerManager.aliveTeams()) {
            double theta = ((double) index++ / this.playerManager.aliveTeamsCount()) * 2 * Math.PI;

            int x = Mth.floor(Math.cos(theta) * (this.timers.getStartMapSize() / 2 - this.spawnOffset));
            int z = Mth.floor(Math.sin(theta) * (this.timers.getStartMapSize() / 2 - this.spawnOffset));

            this.spawnLogic.summonCage(team, x, z);
            this.playerManager.teamPlayers(team.key()).forEach(player -> this.spawnLogic.putParticipantInCage(team, player));
        }
    }

    private void dropCages() {
        this.spawnLogic.clearCages();
        this.setInteractWithLevel(true);

        this.playerManager.forEachAlive((player -> {
            player.setGameMode(GameType.SURVIVAL);
            this.refreshPlayerAttributes(player);
            this.clearPlayer(player);
            this.applyPlayerEffects(player);
        }));
    }

    // GENERAL LISTENERS
    private void enableModule(Holder<UHCModule> moduleRegistryEntry) {
        UHCModule module = moduleRegistryEntry.value();
        for (Modifier modifier : module.modifiers()) {
            modifier.enable(this.playerManager);
        }
        msg.moduleAnnouncement("text.uhc.module.enabled", moduleRegistryEntry, ChatFormatting.GREEN);
    }

    private void disableModule(Holder<UHCModule> moduleRegistryEntry) {
        UHCModule module = moduleRegistryEntry.value();
        for (Modifier modifier : module.modifiers()) {
            modifier.disable(this.playerManager);
        }
        msg.moduleAnnouncement("text.uhc.module.disabled", moduleRegistryEntry, ChatFormatting.RED);
    }

    private EventResult onPlayerDamage(ServerPlayer entity, DamageSource damageSource, float v) {
        if (this.invulnerable) {
            return EventResult.DENY;
        } else {
            return EventResult.PASS;
        }
    }

    private EventResult onPlayerDeath(ServerPlayer player, DamageSource source) {
        if (playerManager.contains(player)) {
            if (!playerManager.get(player).isEliminated()) {
                msg.death(source, player);
                this.eliminateParticipant(player);
                return EventResult.DENY;
            }
        }
        this.spawnLogic.spawnPlayerAtCenter(player);
        if (source.getEntity() instanceof ServerPlayer attacker && this.playerManager.get(attacker) instanceof UHCParticipant participant) {
            participant.addKill();
        }
        return EventResult.DENY;
    }
}
