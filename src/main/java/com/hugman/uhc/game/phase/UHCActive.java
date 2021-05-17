package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCBar;
import com.hugman.uhc.game.UHCLogic;
import com.hugman.uhc.game.UHCParticipant;
import com.hugman.uhc.game.UHCSideBar;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.map.UHCMap;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.EntityLootModulePiece;
import com.hugman.uhc.util.TickUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLogic;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UHCActive {
	public final GameSpace gameSpace;
	private final GameLogic gameLogic;
	private final UHCMap map;
	private final UHCConfig config;

	private final Set<UHCParticipant> participants;

	private final UHCLogic logic;
	private final UHCSpawner spawnLogic;
	private final UHCBar bar;
	private final UHCSideBar sideBar;

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

	private UHCActive(GameLogic gameLogic, UHCMap map, UHCConfig config, GlobalWidgets widgets) {
		this.gameLogic = gameLogic;
		this.gameSpace = this.gameLogic.getSpace();
		this.map = map;
		this.config = config;

		this.participants = this.gameSpace.getPlayers().stream().map(UHCParticipant::new).collect(Collectors.toSet());

		this.logic = new UHCLogic(config, this.participants.size());
		this.spawnLogic = new UHCSpawner(this.gameSpace, this.config);
		this.bar = UHCBar.create(widgets, this.gameSpace);
		this.sideBar = UHCSideBar.create(widgets, this);
	}

	public static void start(GameSpace gameSpace, UHCMap map, UHCConfig config) {
		gameSpace.openGame(game -> {
			GlobalWidgets widgets = new GlobalWidgets(game);
			UHCActive active = new UHCActive(game, map, config, widgets);

			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.DENY);
			game.setRule(GameRule.BLOCK_DROPS, RuleResult.ALLOW);
			game.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
			game.setRule(GameRule.HUNGER, RuleResult.ALLOW);

			game.on(GameOpenListener.EVENT, active::open);
			game.on(GameCloseListener.EVENT, active::close);

			game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
			game.on(PlayerAddListener.EVENT, active::addSpectator);
			game.on(PlayerRemoveListener.EVENT, active::removePlayer);

			game.on(GameTickListener.EVENT, active::tick);

			game.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);

			game.on(EntityDropLootListener.EVENT, active::onMobLoot);
			game.on(BreakBlockListener.EVENT, active::onBlockBroken);
			game.on(ExplosionListener.EVENT, active::onExplosion);
		});
	}

	// GENERAL GAME MANAGEMENT
	private void open() {
		ServerWorld world = this.gameSpace.getWorld();

		// Setup
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(this.logic.getStartMapSize());
		world.getWorldBorder().setDamagePerBlock(0.5);
		this.participants.forEach(player -> player.getPlayer().networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE)));

		this.gameStartTick = world.getTime();
		this.startInvulnerableTick = world.getTime() + this.logic.getInCagesTime();
		this.startWarmupTick = this.startInvulnerableTick + this.logic.getInvulnerabilityTime();
		this.finaleCagesTick = this.startWarmupTick + this.logic.getWarmupTime();
		this.finaleInvulnerabilityTick = this.finaleCagesTick + this.logic.getInCagesTime();
		this.reducingTick = this.finaleInvulnerabilityTick + this.logic.getInvulnerabilityTime();
		this.deathMatchTick = this.reducingTick + this.logic.getShrinkingTime();
		this.gameEndTick = this.deathMatchTick + this.logic.getDeathmatchTime();
		this.gameCloseTick = this.gameEndTick + 600;

		// Start - Cage chapter
		this.participants.forEach(player -> {
			player.reset();
			player.refreshAttributes(this.config);
			player.setAdventure();
		});
		this.tpToCages();
		this.bar.set("text.uhc.dropping", this.logic.getInCagesTime(), this.startInvulnerableTick, BossBar.Color.PURPLE);
	}

	private void tick() {
		ServerWorld world = this.gameSpace.getWorld();

		this.bar.tick();
		this.sideBar.update(world.getTime() - this.gameStartTick, (int) world.getWorldBorder().getSize());

		// Game ends
		if(isFinished) {
			if(world.getTime() > this.gameCloseTick) {
				this.gameSpace.close(GameCloseReason.FINISHED);
			}
			return;
		}

		// Start - Cage chapter (@ 80%)
		if(world.getTime() == this.startInvulnerableTick - (logic.getInCagesTime() * 0.8)) {
			this.sendModuleListToChat();
		}
		// Start - Invulnerable chapter
		else if(world.getTime() == this.startInvulnerableTick) {
			this.dropCages();
			this.sendInfo("text.uhc.dropped_players");
			this.sendInfo("text.uhc.world_will_shrink", TickUtil.formatPretty(this.finaleCagesTick - world.getTime()));

			this.bar.set("🛡", "text.uhc.vulnerable", this.logic.getInvulnerabilityTime(), this.startWarmupTick, BossBar.Color.YELLOW);
		}

		// Start - Warmup chapter
		else if(world.getTime() == this.startWarmupTick) {
			this.setInvulnerable(false);
			this.sendWarning("🛡", "text.uhc.no_longer_immune");

			this.bar.set("text.uhc.tp", this.logic.getWarmupTime(), this.finaleCagesTick, BossBar.Color.BLUE);
		}

		// Finale - Cages chapter
		else if(world.getTime() == this.finaleCagesTick) {
			this.participants.forEach(player -> {
				player.clear();
				player.refreshAttributes(this.config);
				player.setAdventure();
			});
			this.tpToCages();
			this.sendInfo("text.uhc.shrinking_when_pvp");

			this.bar.set("text.uhc.dropping", this.logic.getInCagesTime(), this.finaleInvulnerabilityTick, BossBar.Color.PURPLE);
		}

		// Finale - Invulnerability chapter
		else if(world.getTime() == this.finaleInvulnerabilityTick) {
			this.dropCages();
			this.sendInfo("text.uhc.dropped_players");

			this.bar.set("🗡", "text.uhc.pvp", this.logic.getInvulnerabilityTime(), this.reducingTick, BossBar.Color.YELLOW);
		}

		// Finale - Reducing chapter
		else if(world.getTime() == this.reducingTick) {
			this.setInvulnerable(false);
			this.sendWarning("🛡", "text.uhc.no_longer_immune");

			this.setPvp(true);
			this.sendWarning("🗡", "text.uhc.pvp_enabled");

			world.getWorldBorder().interpolateSize(this.logic.getStartMapSize(), this.logic.getEndMapSize(), this.logic.getShrinkingTime() * 50L);
			this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.LERP_SIZE)));
			this.sendWarning("text.uhc.shrinking_start");

			this.bar.set("text.uhc.shrinking_finish", this.logic.getShrinkingTime(), this.deathMatchTick, BossBar.Color.RED);
		}

		// Finale - Deathmatch chapter
		else if(world.getTime() == this.deathMatchTick) {
			this.bar.setFull(new LiteralText("🗡").append(new TranslatableText("text.uhc.deathmatch")).append("🗡"));
			world.getWorldBorder().setDamagePerBlock(2.5);
			world.getWorldBorder().setBuffer(0.125);
			this.sendInfo("🗡", "text.uhc.last_one_wins");
			this.checkForWinner();
		}
	}

	private void close() {
		for(ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	// GENERAL PLAYER MANAGEMENT
	public Set<UHCParticipant> getParticipants() {
		return participants;
	}

	private UHCParticipant getParticipant(ServerPlayerEntity player) {
		for(UHCParticipant participant : this.participants) {
			if(player == participant.getPlayer()) {
				return participant;
			}
		}
		return null;
	}

	private void addSpectator(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(this.gameSpace.getWorld().getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));
		player.setGameMode(GameMode.SPECTATOR);
		this.spawnLogic.spawnPlayerAtCenter(player);
	}

	private void removePlayer(ServerPlayerEntity player) {
		UHCParticipant participant = getParticipant(player);
		if(participant != null) {
			PlayerSet players = this.gameSpace.getPlayers();
			players.sendMessage(new LiteralText("\n☠ ").append(new TranslatableText("text.uhc.player_eliminated", player.getDisplayName())).append(new LiteralText("\n")).formatted(Formatting.DARK_RED));
			players.sendSound(SoundEvents.ENTITY_WITHER_SPAWN);
			this.eliminateParticipant(participant);
		}
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		UHCParticipant participant = getParticipant(player);
		if(participant != null) {
			PlayerSet players = this.gameSpace.getPlayers();
			players.sendMessage(new LiteralText("\n☠ ").append(source.getDeathMessage(player).copy()).append(new LiteralText("!\n")).formatted(Formatting.DARK_RED));
			players.sendSound(SoundEvents.ENTITY_WITHER_SPAWN);
			this.eliminateParticipant(participant);
		}
		else {
			this.spawnLogic.spawnPlayerAtCenter(player);
		}
		return ActionResult.FAIL;
	}

	private void eliminateParticipant(UHCParticipant participant) {
		ItemScatterer.spawn(this.gameSpace.getWorld(), participant.getPlayer().getBlockPos(), participant.getPlayer().inventory);
		participant.setSpectator();
		this.spawnLogic.spawnPlayerAtCenter(participant.getPlayer());
		this.participants.remove(participant);
		this.checkForWinner();
	}

	private void checkForWinner() {
		PlayerSet players = this.gameSpace.getPlayers();

		if(this.participants.size() <= 1) {
			Optional<UHCParticipant> oParticipant = this.participants.stream().findFirst();
			if(oParticipant.isPresent()) {
				players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.player_win", oParticipant.get().getName()).formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
				oParticipant.get().setAdventure();
				this.setInvulnerable(true);
				this.setPvp(false);
			}
			else {
				players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.none_win").formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
			}
			players.sendSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
			this.gameCloseTick = this.gameSpace.getWorld().getTime() + 200;
			this.bar.close();
			this.isFinished = true;
			this.participants.clear();
		}
	}

	// GAME STATES
	private void setInvulnerable(boolean b) {
		this.invulnerable = b;
		this.gameLogic.setRule(GameRule.HUNGER, b ? RuleResult.DENY : RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.FALL_DAMAGE, b ? RuleResult.DENY : RuleResult.ALLOW);
	}

	private void setPvp(boolean b) {
		this.gameLogic.setRule(GameRule.PVP, b ? RuleResult.ALLOW : RuleResult.DENY);
	}

	private void tpToCages() {
		this.setInvulnerable(true);
		this.gameLogic.setRule(GameRule.BREAK_BLOCKS, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.PLACE_BLOCKS, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.INTERACTION, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.CRAFTING, RuleResult.DENY);

		int index = 0;
		for(UHCParticipant player : this.participants) {
			double theta = ((double) index++ / this.participants.size()) * 2 * Math.PI;

			int x = MathHelper.floor(Math.cos(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));
			int z = MathHelper.floor(Math.sin(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));

			this.spawnLogic.summonPlayerInCageAt(player, x, z);
		}
	}

	private void dropCages() {
		this.spawnLogic.clearCages();
		this.gameLogic.setRule(GameRule.BREAK_BLOCKS, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.PLACE_BLOCKS, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.CRAFTING, RuleResult.ALLOW);

		this.participants.forEach(player -> {
			player.setSurvival();
			player.refreshAttributes(this.config);
			player.clear();
			player.applyEffects(this.config, (int) this.gameEndTick);
		});
	}

	// MESSAGES
	private void sendMessage(String symbol, String s, Formatting f, Object... args) {
		this.gameSpace.getPlayers().sendMessage(new LiteralText(symbol).append(new TranslatableText(s, args)).formatted(f));
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
		if(!this.config.getModules().isEmpty()) {
			MutableText text = new LiteralText("\n").append(new TranslatableText("text.uhc.modules_enabled").formatted(Formatting.GOLD));
			this.config.getModules().forEach(module -> {
				Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText(module.getDescription())));
				text.append(new LiteralText("\n  - ").formatted(Formatting.WHITE)).append(Texts.bracketed(new TranslatableText(module.getTranslation()).formatted(Formatting.GREEN)).setStyle(style));
			});
			text.append("\n");
			this.gameSpace.getPlayers().sendMessage(text);
			this.gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_ITEM_PICKUP);
		}
	}

	// GENERAL LISTENERS
	private ActionResult onPlayerDamage(ServerPlayerEntity entity, DamageSource damageSource, float v) {
		if(this.invulnerable) {
			return ActionResult.FAIL;
		}
		else {
			return ActionResult.SUCCESS;
		}
	}

	private ActionResult onBlockBroken(@Nullable ServerPlayerEntity player, BlockPos origin) {
		for(BucketBreakModulePiece piece : this.config.bucketBreakModulePieces) {
			piece.breakBlock(this, player, origin);
		}
		return ActionResult.SUCCESS;
	}

	private void onExplosion(List<BlockPos> positions) {
		positions.forEach(pos -> onBlockBroken(null, pos));
	}

	private TypedActionResult<List<ItemStack>> onMobLoot(LivingEntity livingEntity, List<ItemStack> itemStacks) {
		boolean replaceDrops = false;
		List<ItemStack> stacks = new ArrayList<>();
		for(EntityLootModulePiece piece : this.config.entityLootModulePieces) {
			if(piece.test(livingEntity)) {
				replaceDrops = true;
				stacks.addAll(piece.getLoots(this.gameSpace.getWorld(), livingEntity));
			}
		}
		if(replaceDrops) {
			return TypedActionResult.pass(stacks);
		}
		else {
			itemStacks.addAll(stacks);
		}
		return TypedActionResult.success(itemStacks);
	}
}
