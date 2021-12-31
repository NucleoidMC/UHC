package com.hugman.uhc.game.phase;

import com.hugman.uhc.UHC;
import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCBar;
import com.hugman.uhc.game.UHCLogic;
import com.hugman.uhc.game.UHCParticipant;
import com.hugman.uhc.game.UHCSideBar;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.map.UHCMap;
import com.hugman.uhc.module.piece.BlockLootModulePiece;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.EntityLootModulePiece;
import com.hugman.uhc.module.piece.PermanentEffectModulePiece;
import com.hugman.uhc.module.piece.PlayerAttributeModulePiece;
import com.hugman.uhc.util.TickUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.explosion.Explosion;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameActivity;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.team.*;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class UHCActive {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final GameActivity activity;
	private final UHCConfig config;
	private final UHCMap map;

	private Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants;
	private List<GameTeam> teamsAlive;
	private TeamManager teamManager;

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

	private UHCActive(GameActivity activity, GameSpace gameSpace, ServerWorld world, UHCConfig config, UHCMap map, GlobalWidgets widgets) {
		this.activity = activity;
		this.gameSpace = gameSpace;
		this.world = world;
		this.config = config;
		this.map = map;

		fillTeams();
		TeamChat.addTo(activity, teamManager);

		this.logic = new UHCLogic(config, this.participants.size());
		this.spawnLogic = new UHCSpawner(this.world);
		this.bar = UHCBar.create(widgets, this.gameSpace);
		this.sideBar = UHCSideBar.create(widgets, gameSpace);
	}

	private void fillTeams() {
		this.teamManager = TeamManager.addTo(activity);
		this.teamsAlive = new ArrayList<>();

		List<DyeColor> teamColors = Arrays.stream(DyeColor.values()).collect(Collectors.toList());
		teamColors.remove(DyeColor.WHITE);
		teamColors.remove(DyeColor.BLACK);
		teamColors.remove(DyeColor.MAGENTA);
		Collections.shuffle(teamColors);

		for (int i = 0; i < Math.round(this.gameSpace.getPlayers().size() / (float) config.teamSize()); i++) {
			GameTeam gameTeam = new GameTeam(new GameTeamKey(RandomStringUtils.randomAlphabetic(16)),
					GameTeamConfig.builder()
							.setFriendlyFire(false)
							.setCollision(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)
							.setName(new LiteralText("UHC Team"))
							.setColors(GameTeamConfig.Colors.from(teamColors.get(i)))
							.build());

			teamManager.addTeam(gameTeam);
			teamsAlive.add(gameTeam);
		}

		TeamAllocator<GameTeam, ServerPlayerEntity> allocator = new TeamAllocator<>(this.teamsAlive);
		this.participants = new Object2ObjectOpenHashMap<>();

		for (ServerPlayerEntity playerEntity : gameSpace.getPlayers()) {
			allocator.add(playerEntity, null);
		}
		allocator.allocate((gameTeam, playerEntity) -> {
			teamManager.addPlayerTo(playerEntity, gameTeam.key());
			UHCParticipant participant = new UHCParticipant();
			participants.put(playerEntity, participant);
		});
	}

	public static void start(GameSpace gameSpace, ServerWorld world, UHCConfig config, UHCMap map) {
		gameSpace.setActivity(activity -> {
			GlobalWidgets widgets = GlobalWidgets.addTo(activity);
			UHCActive active = new UHCActive(activity, gameSpace, world, config, map, widgets);

			activity.allow(GameRuleType.CRAFTING);
			activity.deny(GameRuleType.PORTALS);
			activity.deny(GameRuleType.PVP);
			activity.allow(GameRuleType.BLOCK_DROPS);
			activity.allow(GameRuleType.FALL_DAMAGE);
			activity.allow(GameRuleType.HUNGER);

			activity.listen(GameActivityEvents.ENABLE, active::enable);

			activity.listen(GamePlayerEvents.OFFER, active::offerPlayer);
			activity.listen(GamePlayerEvents.LEAVE, active::playerLeave);

			activity.listen(GameActivityEvents.TICK, active::tick);

			activity.listen(PlayerDamageEvent.EVENT, active::onPlayerDamage);
			activity.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);

			activity.listen(EntityDropItemsEvent.EVENT, active::onMobLoot);
			activity.listen(BlockBreakEvent.EVENT, active::onBlockBroken);
			activity.listen(BlockDropItemsEvent.EVENT, active::onBlockDrop);
			activity.listen(ExplosionDetonatedEvent.EVENT, active::onExplosion);
		});
	}

	// GENERAL GAME MANAGEMENT
	private void enable() {
		ServerWorld world = this.world;

		// Setup
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(this.logic.getStartMapSize());
		world.getWorldBorder().setDamagePerBlock(0.5);
		this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(world.getWorldBorder())));

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
		this.participants.forEach((player, participant) -> {
			if(!participant.isEliminated()) {
				this.resetPlayer(player);
				this.refreshPlayerAttributes(player);
				player.changeGameMode(GameMode.ADVENTURE);
			}
		});
		this.tpToCages();
		this.bar.set("text.uhc.dropping", this.logic.getInCagesTime(), this.startInvulnerableTick, BossBar.Color.PURPLE);
	}

	private void tick() {
		ServerWorld world = this.world;
		long worldTime = world.getTime();

		this.bar.tick(world);
		this.sideBar.update(worldTime - this.gameStartTick, (int) world.getWorldBorder().getSize(), this.participants);

		// Game ends
		if(isFinished) {
			if(worldTime > this.gameCloseTick) {
				this.gameSpace.close(GameCloseReason.FINISHED);
			}
			return;
		}

		// Start - Cage chapter (@ 80%)
		if(worldTime == this.startInvulnerableTick - (logic.getInCagesTime() * 0.8)) {
			this.sendModuleListToChat();
		}
		// Start - Invulnerable chapter
		else if(worldTime == this.startInvulnerableTick) {
			this.dropCages();
			this.sendInfo("text.uhc.dropped_players");
			this.sendInfo("text.uhc.world_will_shrink", TickUtil.formatPretty(this.finaleCagesTick - worldTime));

			this.bar.set("🛡", "text.uhc.vulnerable", this.logic.getInvulnerabilityTime(), this.startWarmupTick, BossBar.Color.YELLOW);
		}

		// Start - Warmup chapter
		else if(worldTime == this.startWarmupTick) {
			this.setInvulnerable(false);
			this.sendWarning("🛡", "text.uhc.no_longer_immune");

			this.bar.set("text.uhc.tp", this.logic.getWarmupTime(), this.finaleCagesTick, BossBar.Color.BLUE);
		}

		// Finale - Cages chapter
		else if(worldTime == this.finaleCagesTick) {
			this.participants.forEach((player, participant) -> {
				if(!participant.isEliminated()) {
					this.clearPlayer(player);
					this.refreshPlayerAttributes(player);
					player.changeGameMode(GameMode.ADVENTURE);
				}
			});
			this.tpToCages();
			this.sendInfo("text.uhc.shrinking_when_pvp");

			this.bar.set("text.uhc.dropping", this.logic.getInCagesTime(), this.finaleInvulnerabilityTick, BossBar.Color.PURPLE);
		}

		// Finale - Invulnerability chapter
		else if(worldTime == this.finaleInvulnerabilityTick) {
			this.dropCages();
			this.sendInfo("text.uhc.dropped_players");

			this.bar.set("🗡", "text.uhc.pvp", this.logic.getInvulnerabilityTime(), this.reducingTick, BossBar.Color.YELLOW);
		}

		// Finale - Reducing chapter
		else if(worldTime == this.reducingTick) {
			this.setInvulnerable(false);
			this.sendWarning("🛡", "text.uhc.no_longer_immune");

			this.setPvp(true);
			this.sendWarning("🗡", "text.uhc.pvp_enabled");

			world.getWorldBorder().interpolateSize(this.logic.getStartMapSize(), this.logic.getEndMapSize(), this.logic.getShrinkingTime() * 50L);
			this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderInterpolateSizeS2CPacket(world.getWorldBorder())));
			this.sendWarning("text.uhc.shrinking_start");

			this.bar.set("text.uhc.shrinking_finish", this.logic.getShrinkingTime(), this.deathMatchTick, BossBar.Color.RED);
		}

		// Finale - Deathmatch chapter
		else if(worldTime == this.deathMatchTick) {
			this.bar.setFull(new LiteralText("🗡").append(new TranslatableText("text.uhc.deathmatchTime")).append("🗡"));
			world.getWorldBorder().setDamagePerBlock(2.5);
			world.getWorldBorder().setSafeZone(0.125);
			this.sendInfo("🗡", "text.uhc.last_one_wins");
			this.checkForWinner();
		}
	}

	// GENERAL PLAYER MANAGEMENT
	private UHCParticipant getParticipant(ServerPlayerEntity player) {
		return participants.get(player);
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.world, UHCSpawner.getSurfaceBlock(world, 0, 0)).and(() -> {
			ServerPlayerEntity player = offer.player();

			player.changeGameMode(GameMode.SPECTATOR);
			player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(this.world.getWorldBorder()));
		});
	}

	private void playerLeave(ServerPlayerEntity player) {
		if(participants.containsKey(player)) {
			if(!getParticipant(player).isEliminated()) {
				PlayerSet players = this.gameSpace.getPlayers();
				players.sendMessage(new LiteralText("\n☠ ").append(new TranslatableText("text.uhc.player_eliminated", player.getDisplayName())).append("\n").formatted(Formatting.DARK_RED));
				players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
				this.eliminateParticipant(player);
			}
		}
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		if(participants.containsKey(player)) {
			if(!getParticipant(player).isEliminated()) {
				PlayerSet players = this.gameSpace.getPlayers();
				players.sendMessage(new LiteralText("\n☠ ").append(source.getDeathMessage(player).copy()).append("!\n").formatted(Formatting.DARK_RED));
				players.playSound(SoundEvents.ENTITY_WITHER_SPAWN);
				this.eliminateParticipant(player);
				return ActionResult.FAIL;
			}
		}
		this.spawnLogic.spawnPlayerAtCenter(player);
		return ActionResult.FAIL;
	}

	private void eliminateParticipant(ServerPlayerEntity player) {
		ItemScatterer.spawn(player.getWorld(), player.getBlockPos(), player.getInventory());
		player.changeGameMode(GameMode.SPECTATOR);
		this.resetPlayer(player);
		this.spawnLogic.spawnPlayerAtCenter(player);
		getParticipant(player).eliminate();
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
		for(PlayerAttributeModulePiece piece : this.config.playerAttributeModulePieces) {
			piece.setAttribute(player);
		}
	}

	public void applyPlayerEffects(ServerPlayerEntity player, int effectDuration) {
		for(PermanentEffectModulePiece piece : this.config.permanentEffectModulePieces) {
			piece.setEffect(player, effectDuration);
		}
	}

	private void checkForWinner() {
		PlayerSet players = this.gameSpace.getPlayers();

		// Remove empty teams
		teamsAlive.removeIf(team -> teamManager.playersIn(team.key()).stream().allMatch(playerEntity -> getParticipant(playerEntity).isEliminated()));
		// Only one team is left, so they win
		if(teamsAlive.size() <= 1) {
			if(teamsAlive.size() <= 0) {
				players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.none_win").formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
				UHC.LOGGER.warn("There are no teams left! Consider reviewing the minimum amount of players needed to start a game, so that there are at least 2 teams in the game.");
			}
			else {
				GameTeam lastTeam = teamsAlive.get(0);
				PlayerSet teamMembers = teamManager.playersIn(lastTeam.key());
				if(teamMembers.size() <= 0) {
					players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.none_win").formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
					UHC.LOGGER.warn("There is only one team left, but there are no players in it!");
				}
				else if(teamMembers.size() == 1) {
					Optional<ServerPlayerEntity> participant = teamMembers.stream().findFirst();
					participant.ifPresent(playerEntity -> players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.player_win.solo", playerEntity.getName()).formatted(Formatting.BOLD, Formatting.GOLD)).append("\n")));
				}
				else {
					players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.player_win.team", Texts.join(teamMembers.stream().toList(), PlayerEntity::getName)).formatted(Formatting.BOLD, Formatting.GOLD)).append("\n"));
				}
				teamMembers.forEach(playerEntity -> playerEntity.changeGameMode(GameMode.ADVENTURE));
				this.setInvulnerable(true);
				this.setPvp(false);
			}
			players.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
			this.gameCloseTick = this.world.getTime() + 200;
			this.bar.close();
			this.isFinished = true;
			this.participants.clear();
		}
	}

	// GAME STATES
	private void setInvulnerable(boolean b) {
		this.invulnerable = b;
		this.activity.setRule(GameRuleType.HUNGER, b ? ActionResult.FAIL : ActionResult.SUCCESS);
		this.activity.setRule(GameRuleType.FALL_DAMAGE, b ? ActionResult.FAIL : ActionResult.SUCCESS);
	}

	private void setPvp(boolean b) {
		this.activity.setRule(GameRuleType.PVP, b ? ActionResult.SUCCESS : ActionResult.FAIL);
	}

	private void setInteractWithWorld(boolean b) {
		this.activity.setRule(GameRuleType.BREAK_BLOCKS, b ? ActionResult.SUCCESS : ActionResult.FAIL);
		this.activity.setRule(GameRuleType.PLACE_BLOCKS, b ? ActionResult.SUCCESS : ActionResult.FAIL);
		this.activity.setRule(GameRuleType.INTERACTION, b ? ActionResult.SUCCESS : ActionResult.FAIL);
		this.activity.setRule(GameRuleType.CRAFTING, b ? ActionResult.SUCCESS : ActionResult.FAIL);
	}

	private void tpToCages() {
		this.setInvulnerable(true);
		this.setInteractWithWorld(false);

		int index = 0;
		for(GameTeam team : teamsAlive) {
			double theta = ((double) index++ / teamsAlive.size()) * 2 * Math.PI;

			int x = MathHelper.floor(Math.cos(theta) * (this.logic.getStartMapSize() / 2 - this.config.mapConfig().spawnOffset()));
			int z = MathHelper.floor(Math.sin(theta) * (this.logic.getStartMapSize() / 2 - this.config.mapConfig().spawnOffset()));

			this.spawnLogic.summonCage(team, x, z);
			teamManager.playersIn(team.key()).forEach(player -> this.spawnLogic.putParticipantInCage(team, player));
		}
	}

	private void dropCages() {
		this.spawnLogic.clearCages();
		this.setInteractWithWorld(true);

		this.participants.forEach((player, participant) -> {
			if(!participant.isEliminated()) {
				player.changeGameMode(GameMode.SURVIVAL);
				this.refreshPlayerAttributes(player);
				this.clearPlayer(player);
				this.applyPlayerEffects(player, (int) this.gameEndTick);
			}
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
		if(!this.config.modules().isEmpty()) {
			MutableText text = new LiteralText("\n").append(new TranslatableText("text.uhc.modules_enabled").formatted(Formatting.GOLD));
			this.config.modules().forEach(module -> {
				MutableText descriptionLines = new LiteralText("");
				module.getDescriptionLines().forEach(s -> descriptionLines.append(new TranslatableText(s)));
				Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, descriptionLines)).withColor(module.color());
				text.append(new LiteralText("\n  - ").formatted(Formatting.WHITE)).append(Texts.bracketed(new TranslatableText(module.translation())).setStyle(style));
			});
			text.append("\n");
			this.gameSpace.getPlayers().sendMessage(text);
			this.gameSpace.getPlayers().playSound(SoundEvents.ENTITY_ITEM_PICKUP);
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

	private ActionResult onBlockBroken(ServerPlayerEntity playerEntity, ServerWorld world, BlockPos pos) {
		for(BucketBreakModulePiece piece : this.config.bucketBreakModulePieces) {
			piece.breakBlock(this.world, playerEntity, pos);
		}
		return ActionResult.SUCCESS;
	}

	private void onExplosion(Explosion explosion, boolean b) {
		explosion.getAffectedBlocks().forEach(pos -> {
			for(BucketBreakModulePiece piece : this.config.bucketBreakModulePieces) {
				piece.breakBlock(this.world, explosion.getCausingEntity(), pos);
			}
		});
	}

	private TypedActionResult<List<ItemStack>> onMobLoot(LivingEntity livingEntity, List<ItemStack> itemStacks) {
		boolean keepOld = true;
		List<ItemStack> stacks = new ArrayList<>();
		for(EntityLootModulePiece piece : this.config.entityLootModulePieces) {
			if(piece.test(livingEntity)) {
				stacks.addAll(piece.getLoots(this.world, livingEntity));
				if(piece.replace()) keepOld = false;
			}
		}
		if(keepOld) stacks.addAll(itemStacks);
		return TypedActionResult.pass(stacks);
	}

	private TypedActionResult<List<ItemStack>> onBlockDrop(@Nullable Entity entity, ServerWorld world, BlockPos pos, BlockState state, List<ItemStack> itemStacks) {
		boolean keepOld = true;
		List<ItemStack> stacks = new ArrayList<>();
		for(BlockLootModulePiece piece : this.config.blockLootModulePieces) {
			if(piece.test(state, world.getRandom())) {
				piece.spawnExperience(world, pos);
				stacks.addAll(piece.getLoots(world, pos, entity, entity instanceof LivingEntity ? ((LivingEntity) entity).getActiveItem() : ItemStack.EMPTY));
				if(piece.replace()) keepOld = false;
			}
		}
		if(keepOld) stacks.addAll(itemStacks);
		return TypedActionResult.pass(stacks);
	}
}
