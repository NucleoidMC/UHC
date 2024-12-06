package com.hugman.uhc.game.phase;

import com.hugman.uhc.UHC;
import com.hugman.uhc.config.UHCGameConfig;
import com.hugman.uhc.game.*;
import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.modifier.ModifierType;
import com.hugman.uhc.modifier.PermanentEffectModifier;
import com.hugman.uhc.modifier.PlayerAttributeModifier;
import com.hugman.uhc.module.Module;
import com.hugman.uhc.module.ModuleEvents;
import com.hugman.uhc.util.TickUtil;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
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
    private final ServerWorld world;
    private final GameActivity activity;
    private final int spawnOffset;

    private final UHCPlayerManager playerManager;
    private final UHCTimers timers;
    private final UHCSpawner spawnLogic;
    private final UHCBar bar;
    private final UHCSideBar sideBar;
    private final ModuleManager moduleManager;

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
            GameActivity activity,
            GameSpace gameSpace,
            ServerWorld world,
            UHCGameConfig config,
            GlobalWidgets widgets,
            UHCPlayerManager playerManager,
            ModuleManager moduleManager
    ) {
        this.activity = activity;
        this.gameSpace = gameSpace;
        this.world = world;
        this.playerManager = playerManager;
        this.moduleManager = moduleManager;

        this.spawnOffset = config.uhcConfig().value().mapConfig().spawnOffset();
        this.timers = new UHCTimers(config, this.playerManager.participantCount());
        this.spawnLogic = new UHCSpawner(this.world);
        this.bar = UHCBar.create(widgets, this.gameSpace);
        this.sideBar = UHCSideBar.create(widgets, gameSpace);
    }

    public static void start(GameSpace gameSpace, ServerWorld world, UHCGameConfig config) {
        gameSpace.setActivity(activity -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(activity);
            var moduleManager = gameSpace.getAttachment(UHCAttachments.MODULE_MANAGER);
            assert moduleManager != null;
            var playerManager = UHCPlayerManager.of(activity, gameSpace, config);
            UHCActive active = new UHCActive(
                    activity,
                    gameSpace,
                    world,
                    config,
                    widgets,
                    playerManager,
                    moduleManager
            );

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
            activity.listen(ModuleEvents.ENABLE, active::enableModule);
            activity.listen(ModuleEvents.DISABLE, active::disableModule);

            activity.listen(PlayerDamageEvent.EVENT, active::onPlayerDamage);
            activity.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);

            moduleManager.setupListeners(activity);
        });
    }

    // GENERAL GAME MANAGEMENT
    private void enable() {
        ServerWorld world = this.world;

        // Setup
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(this.timers.getStartMapSize());
        world.getWorldBorder().setDamagePerBlock(0.5);
        this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(world.getWorldBorder())));

        this.gameStartTick = world.getTime();
        this.startInvulnerableTick = world.getTime() + this.timers.getInCagesTime();
        this.startWarmupTick = this.startInvulnerableTick + this.timers.getInvulnerabilityTime();
        this.finaleCagesTick = this.startWarmupTick + this.timers.getWarmupTime();
        this.finaleInvulnerabilityTick = this.finaleCagesTick + this.timers.getInCagesTime();
        this.reducingTick = this.finaleInvulnerabilityTick + this.timers.getInvulnerabilityTime();
        this.deathMatchTick = this.reducingTick + this.timers.getShrinkingTime();
        this.gameEndTick = this.deathMatchTick + this.timers.getDeathmatchTime();
        this.gameCloseTick = this.gameEndTick + 600;

        // Start - Cage chapter
        this.playerManager.forEachAliveParticipant(player -> {
            this.resetPlayer(player);
            this.refreshPlayerAttributes(player);
            player.changeGameMode(GameMode.ADVENTURE);
        });
        this.tpToCages();
        this.bar.set("text.uhc.dropping", this.timers.getInCagesTime(), this.startInvulnerableTick, BossBar.Color.PURPLE);
    }

    private void tick() {
        ServerWorld world = this.world;
        long worldTime = world.getTime();

        this.bar.tick(world);
        this.sideBar.update(worldTime - this.gameStartTick, (int) world.getWorldBorder().getSize(), this.playerManager);

        // Game ends
        if (isFinished) {
            if (worldTime > this.gameCloseTick) {
                this.gameSpace.close(GameCloseReason.FINISHED);
            }
            return;
        }

        // Start - Cage chapter (@ 80%)
        if (worldTime == this.startInvulnerableTick - (timers.getInCagesTime() * 0.8)) {
            this.sendModuleListToChat();
        }
        // Start - Invulnerable chapter
        else if (worldTime == this.startInvulnerableTick) {
            this.dropCages();
            this.sendInfo("text.uhc.dropped_players");
            this.sendInfo("text.uhc.world_will_shrink", TickUtil.formatPretty(this.finaleCagesTick - worldTime));

            this.bar.set("🛡", "text.uhc.vulnerable", this.timers.getInvulnerabilityTime(), this.startWarmupTick, BossBar.Color.YELLOW);
        }

        // Start - Warmup chapter
        else if (worldTime == this.startWarmupTick) {
            this.setInvulnerable(false);
            this.sendWarning("🛡", "text.uhc.no_longer_immune");

            this.bar.set("text.uhc.tp", this.timers.getWarmupTime(), this.finaleCagesTick, BossBar.Color.BLUE);
        }

        // Finale - Cages chapter
        else if (worldTime == this.finaleCagesTick) {
            this.playerManager.forEachAliveParticipant(player -> {
                this.clearPlayer(player);
                this.refreshPlayerAttributes(player);
                player.changeGameMode(GameMode.ADVENTURE);
            });
            this.tpToCages();
            this.sendInfo("text.uhc.shrinking_when_pvp");

            this.bar.set("text.uhc.dropping", this.timers.getInCagesTime(), this.finaleInvulnerabilityTick, BossBar.Color.PURPLE);
        }

        // Finale - Invulnerability chapter
        else if (worldTime == this.finaleInvulnerabilityTick) {
            this.dropCages();
            this.sendInfo("text.uhc.dropped_players");

            this.bar.set("🗡", "text.uhc.pvp", this.timers.getInvulnerabilityTime(), this.reducingTick, BossBar.Color.YELLOW);
        }

        // Finale - Reducing chapter
        else if (worldTime == this.reducingTick) {
            this.setInvulnerable(false);
            this.sendWarning("🛡", "text.uhc.no_longer_immune");

            this.setPvp(true);
            this.sendWarning("🗡", "text.uhc.pvp_enabled");

            world.getWorldBorder().interpolateSize(this.timers.getStartMapSize(), this.timers.getEndMapSize(), this.timers.getShrinkingTime() * 50L);
            this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderInterpolateSizeS2CPacket(world.getWorldBorder())));
            this.sendWarning("text.uhc.shrinking_start");

            this.bar.set("text.uhc.shrinking_finish", this.timers.getShrinkingTime(), this.deathMatchTick, BossBar.Color.RED);
        }

        // Finale - Deathmatch chapter
        else if (worldTime == this.deathMatchTick) {
            this.bar.setFull(Text.literal("🗡").append(Text.translatable("text.uhc.deathmatchTime")).append("🗡"));
            world.getWorldBorder().setDamagePerBlock(2.5);
            world.getWorldBorder().setSafeZone(0.125);
            this.sendInfo("🗡", "text.uhc.last_one_wins");
            this.checkForWinner();
        }
    }

    // GENERAL PLAYER MANAGEMENT
    private JoinAcceptorResult acceptPlayer(JoinAcceptor joinAcceptor) {
        return joinAcceptor
                .teleport(this.world, UHCSpawner.getSurfaceBlock(world, 0, 0))
                .thenRunForEach(player -> {
                    player.changeGameMode(GameMode.SPECTATOR);
                    player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(this.world.getWorldBorder()));
                });
    }

    private void playerLeave(ServerPlayerEntity player) {
        if (playerManager.isParticipant(player)) {
            if (!playerManager.getParticipant(player).isEliminated()) {
                var players = this.gameSpace.getPlayers();
                players.sendMessage(Text.literal("\n☠ ").append(Text.translatable("text.uhc.player_eliminated", player.getDisplayName())).append("\n").formatted(Formatting.DARK_RED));
                players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
                this.eliminateParticipant(player);
            }
        }
    }

    private void eliminateParticipant(ServerPlayerEntity player) {
        ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), player.getInventory());
        player.changeGameMode(GameMode.SPECTATOR);
        this.resetPlayer(player);
        this.spawnLogic.spawnPlayerAtCenter(player);
        playerManager.getParticipant(player).eliminate();
        this.checkForWinner();
    }

    public void resetPlayer(ServerPlayerEntity player) {
        this.clearPlayer(player);
        player.getInventory().clear();
        player.getEnderChestInventory().clear();
        player.clearStatusEffects();
        player.getHungerManager().setFoodLevel(20);
        player.setExperienceLevel(0);
        player.setExperiencePoints(0);
        player.setHealth(player.getMaxHealth());
    }

    public void clearPlayer(ServerPlayerEntity player) {
        player.extinguish();
        player.fallDistance = 0.0F;
    }

    public void refreshPlayerAttributes(ServerPlayerEntity player) {
        for (PlayerAttributeModifier piece : this.moduleManager.getModifiers(ModifierType.PLAYER_ATTRIBUTE)) {
            piece.refreshAttribute(player);
        }
        player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getTracked()));
    }

    public void applyPlayerEffects(ServerPlayerEntity player) {
        for (PermanentEffectModifier piece : this.moduleManager.getModifiers(ModifierType.PERMANENT_EFFECT)) {
            piece.setEffect(player);
        }
    }

    private void checkForWinner() {
        PlayerSet players = this.gameSpace.getPlayers();

        // Remove empty teams
        this.playerManager.refreshAliveTeams();
        // Only one team is left, so they win
        if (this.playerManager.aliveTeamCount() <= 1) {
            if (this.playerManager.noTeamsAlive()) {
                players.sendMessage(Text.literal("\n").append(Text.translatable("text.uhc.none_win").formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
                UHC.LOGGER.warn("There are no teams left! Consider reviewing the minimum amount of players needed to start a game, so that there are at least 2 teams in the game.");
            } else {
                GameTeam lastTeam = this.playerManager.getLastTeam();
                PlayerSet teamMembers = this.playerManager.teamPlayers(lastTeam.key());
                if (teamMembers.size() <= 0) {
                    players.sendMessage(Text.literal("\n").append(Text.translatable("text.uhc.none_win").formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
                    UHC.LOGGER.warn("There is only one team left, but there are no players in it!");
                } else if (teamMembers.size() == 1) {
                    Optional<ServerPlayerEntity> participant = teamMembers.stream().findFirst();
                    participant.ifPresent(playerEntity -> players.sendMessage(Text.literal("\n").append(Text.translatable("text.uhc.player_win.solo", playerEntity.getName()).formatted(Formatting.BOLD, Formatting.GOLD)).append("\n")));
                } else {
                    players.sendMessage(Text.literal("\n").append(Text.translatable("text.uhc.player_win.team", Texts.join(teamMembers.stream().toList(), PlayerEntity::getName)).formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
                }
                teamMembers.forEach(playerEntity -> playerEntity.changeGameMode(GameMode.ADVENTURE));
                this.setInvulnerable(true);
                this.setPvp(false);
            }
            players.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
            this.gameCloseTick = this.world.getTime() + 200;
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

    private void setInteractWithWorld(boolean b) {
        this.activity.setRule(GameRuleType.BREAK_BLOCKS, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.PLACE_BLOCKS, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.INTERACTION, b ? EventResult.ALLOW : EventResult.DENY);
        this.activity.setRule(GameRuleType.CRAFTING, b ? EventResult.ALLOW : EventResult.DENY);
    }

    private void tpToCages() {
        this.setInvulnerable(true);
        this.setInteractWithWorld(false);

        int index = 0;
        for (GameTeam team : this.playerManager.aliveTeams()) {
            double theta = ((double) index++ / this.playerManager.aliveTeamCount()) * 2 * Math.PI;

            int x = MathHelper.floor(Math.cos(theta) * (this.timers.getStartMapSize() / 2 - this.spawnOffset));
            int z = MathHelper.floor(Math.sin(theta) * (this.timers.getStartMapSize() / 2 - this.spawnOffset));

            this.spawnLogic.summonCage(team, x, z);
            this.playerManager.teamPlayers(team.key()).forEach(player -> this.spawnLogic.putParticipantInCage(team, player));
        }
    }

    private void dropCages() {
        this.spawnLogic.clearCages();
        this.setInteractWithWorld(true);

        this.playerManager.forEachAliveParticipant((player -> {
            player.changeGameMode(GameMode.SURVIVAL);
            this.refreshPlayerAttributes(player);
            this.clearPlayer(player);
            this.applyPlayerEffects(player);
        }));
    }

    // MESSAGES
    private void sendMessage(String symbol, String s, Formatting f, Object... args) {
        this.gameSpace.getPlayers().sendMessage(Text.literal(symbol).append(Text.translatable(s, args)).formatted(f));
    }

    public void sendInfo(String symbol, String s, Object... args) {
        this.sendMessage(symbol + " ", s, Formatting.YELLOW, args);
    }

    public void sendInfo(String s, Object... args) {
        this.sendMessage("", s, Formatting.YELLOW, args);
    }

    private void sendWarning(String symbol, String s, Object... args) {
        this.sendMessage(symbol + " ", s, Formatting.RED, args);
    }

    private void sendWarning(String s, Object... args) {
        this.sendMessage("", s, Formatting.RED, args);
    }

    public void sendModuleListToChat() {
        if (!this.moduleManager.isEmpty()) {
            this.gameSpace.getPlayers().sendMessage(this.moduleManager.buildChatMessage());
            this.gameSpace.getPlayers().playSound(SoundEvents.ENTITY_ITEM_PICKUP);
        }
    }

    // GENERAL LISTENERS
    private void enableModule(RegistryEntry<Module> moduleRegistryEntry) {
        Module module = moduleRegistryEntry.value();
        for (Modifier modifier : module.modifiers()) {
            modifier.enable(this.playerManager);
        }
    }

    private void disableModule(RegistryEntry<Module> moduleRegistryEntry) {
        Module module = moduleRegistryEntry.value();
        for (Modifier modifier : module.modifiers()) {
            modifier.disable(this.playerManager);
        }
    }

    private EventResult onPlayerDamage(ServerPlayerEntity entity, DamageSource damageSource, float v) {
        if (this.invulnerable) {
            return EventResult.DENY;
        } else {
            return EventResult.PASS;
        }
    }

    private EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        if (playerManager.isParticipant(player)) {
            if (!playerManager.getParticipant(player).isEliminated()) {
                PlayerSet players = this.gameSpace.getPlayers();
                players.sendMessage(Text.literal("\n☠ ").append(source.getDeathMessage(player).copy()).append("!\n").formatted(Formatting.DARK_RED));
                players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
                this.eliminateParticipant(player);
                return EventResult.DENY;
            }
        }
        this.spawnLogic.spawnPlayerAtCenter(player);
        return EventResult.DENY;
    }
}
