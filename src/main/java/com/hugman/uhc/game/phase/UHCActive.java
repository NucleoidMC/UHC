package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCBar;
import com.hugman.uhc.game.UHCLogic;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.map.UHCMap;
import com.hugman.uhc.module.piece.BlockLootModulePiece;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.EntityLootModulePiece;
import com.hugman.uhc.module.piece.ModulePieceManager;
import net.minecraft.entity.LivingEntity;
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

public class UHCActive {
	public final GameSpace gameSpace;
	private final GameLogic gameLogic;
	private final UHCMap map;
	private final UHCConfig config;
	private final ModulePieceManager modulePieceManager;

	private final PlayerSet participants;

	private final UHCLogic logic;
	private final UHCSpawner spawnLogic;
	private final UHCBar bar;

	private long cagesEndTick;
	private long invulnerabilityEndTick;
	private long peacefulEndTick;
	private long wildEndTick;
	private long preShrinkingCagesEndTick;
	private long preShrinkingInvulnerabilityEndTick;
	private long shrinkingEndTick;
	private long deathmatchEndTick;
	private long gameCloseTick;

	private boolean invulnerable;
	private boolean isFinished = false;

	private UHCActive(GameLogic gameLogic, UHCMap map, UHCConfig config, GlobalWidgets widgets) {
		this.gameLogic = gameLogic;
		this.gameSpace = this.gameLogic.getSpace();
		this.map = map;
		this.config = config;
		this.modulePieceManager = new ModulePieceManager(config);

		this.participants = this.gameSpace.getPlayers();

		this.logic = new UHCLogic(config, this.participants.size());
		this.spawnLogic = new UHCSpawner(this.gameSpace, this.modulePieceManager);
		this.bar = UHCBar.create(widgets, this.gameSpace);
	}

	public static void open(GameSpace gameSpace, UHCMap map, UHCConfig config) {
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
			game.on(PlayerAddListener.EVENT, active::addPlayer);
			game.on(PlayerRemoveListener.EVENT, active::removePlayer);

			game.on(GameTickListener.EVENT, active::tick);

			game.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);

			game.on(EntityDropLootListener.EVENT, active::onMobLoot);
			game.on(BreakBlockListener.EVENT, active::onBlockBroken);
			game.on(ExplosionListener.EVENT, active::onExplosion);
		});
	}

	private void open() {
		ServerWorld world = this.gameSpace.getWorld();

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(this.logic.getStartMapSize());
		world.getWorldBorder().setDamagePerBlock(0.5);

		this.cagesEndTick = world.getTime() + this.logic.getInCagesTime();
		this.invulnerabilityEndTick = this.cagesEndTick + this.logic.getInvulnerabilityTime();
		this.peacefulEndTick = this.invulnerabilityEndTick + this.logic.getPeacefulTime();
		this.wildEndTick = this.peacefulEndTick + this.logic.getWildTime();
		this.preShrinkingCagesEndTick = this.wildEndTick + this.logic.getInCagesTime();
		this.preShrinkingInvulnerabilityEndTick = this.preShrinkingCagesEndTick + this.logic.getInvulnerabilityTime();
		this.shrinkingEndTick = this.preShrinkingInvulnerabilityEndTick + this.logic.getShrinkingTime();
		this.deathmatchEndTick = this.shrinkingEndTick + this.logic.getDeathmatchTime();
		this.gameCloseTick = this.deathmatchEndTick + 600;

		this.participants.forEach(player -> this.spawnLogic.resetPlayer(player, GameMode.SURVIVAL, true));

		this.putPlayersInCages();
	}

	private void close() {
		for(ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	private void tick() {
		ServerWorld world = this.gameSpace.getWorld();

		// Game ends
		if(isFinished) {
			if(world.getTime() > this.gameCloseTick) {
				this.gameSpace.close(GameCloseReason.FINISHED);
			}
			return;
		}

		// Cage chapter
		if(world.getTime() < this.cagesEndTick) {
			this.bar.tickUntilDrop(this.cagesEndTick - world.getTime(), this.logic.getInCagesTime());
			if(world.getTime() == this.cagesEndTick - (logic.getInCagesTime() * 0.8)) {
				this.sendModuleListToChat();
			}
		}
		// Cage chapter ends
		else if(world.getTime() == this.cagesEndTick) {
			this.dropPlayers();
		}
		// Invulnerable chapter
		else if(world.getTime() < this.invulnerabilityEndTick) {
			this.bar.tickUntilVulnerable(this.invulnerabilityEndTick - world.getTime(), this.logic.getInvulnerabilityTime());
		}
		// Invulnerable chapter ends
		else if(world.getTime() == this.invulnerabilityEndTick) {
			this.setInvulnerable(false);
		}
		// Peaceful chapter
		else if(world.getTime() < this.peacefulEndTick) {
			this.bar.tickUntilPvp(this.peacefulEndTick - world.getTime(), this.logic.getPeacefulTime());
		}
		// Peaceful chapter ends
		else if(world.getTime() == this.peacefulEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.pvp_enabled").formatted(Formatting.RED));
			this.gameLogic.setRule(GameRule.INTERACTION, RuleResult.DENY);
		}
		// Wild chapter
		else if(world.getTime() < this.wildEndTick) {
			this.bar.tickUntilTp(this.wildEndTick - world.getTime(), this.logic.getWildTime());
		}
		// Wild chapter ends
		else if(world.getTime() == this.wildEndTick) {
			this.putPlayersInCages();
		}

		// Pre-shrinking cages chapter
		else if(world.getTime() < this.preShrinkingCagesEndTick) {
			this.bar.tickUntilDrop(this.preShrinkingCagesEndTick - world.getTime(), this.logic.getInCagesTime());
		}
		// Pre-shrinking cages chapter ends
		else if(world.getTime() == this.preShrinkingCagesEndTick) {
			this.dropPlayers();
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.invulnerable_until_shrink_start").formatted(Formatting.AQUA));
		}
		// Pre-shrinking invulnerable chapter
		else if(world.getTime() < this.preShrinkingInvulnerabilityEndTick) {
			this.bar.tickUntilShrinkStart(this.preShrinkingInvulnerabilityEndTick - world.getTime(), this.logic.getInvulnerabilityTime());
		}
		// Pre-shrinking invulnerable chapter ends
		else if(world.getTime() == this.preShrinkingInvulnerabilityEndTick) {
			world.getWorldBorder().interpolateSize(this.logic.getStartMapSize(), this.logic.getEndMapSize(), this.logic.getShrinkingTime() * 50L);
			this.gameSpace.getPlayers().forEach(player -> player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.LERP_SIZE)));
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.shrinking_started").formatted(Formatting.RED));
			this.setInvulnerable(false);
		}

		// Shrinking chapter
		else if(world.getTime() < this.shrinkingEndTick) {
			this.bar.tickUntilShrinkFinish(this.shrinkingEndTick - world.getTime(), this.logic.getShrinkingTime());
		}
		// Shrinking chapter ends
		else if(world.getTime() == this.shrinkingEndTick) {
			world.getWorldBorder().setDamagePerBlock(2.5);
			world.getWorldBorder().setBuffer(0.125);
			this.bar.setDeathmatch();
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.last_one_wins").formatted(Formatting.AQUA));
			this.checkForWinner();
		}
	}

	private void setInvulnerable(boolean b) {
		RuleResult r = b ? RuleResult.DENY : RuleResult.ALLOW;
		this.invulnerable = b;
		this.gameLogic.setRule(GameRule.PVP, r);
		this.gameLogic.setRule(GameRule.HUNGER, r);
		this.gameLogic.setRule(GameRule.FALL_DAMAGE, r);

		if(!b) this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.invulnerability_end").formatted(Formatting.RED));
	}

	private void putPlayersInCages() {
		this.setInvulnerable(true);
		this.gameLogic.setRule(GameRule.BREAK_BLOCKS, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.PLACE_BLOCKS, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.INTERACTION, RuleResult.DENY);
		this.gameLogic.setRule(GameRule.CRAFTING, RuleResult.DENY);

		ServerWorld world = this.gameSpace.getWorld();
		int index = 0;
		for(ServerPlayerEntity player : this.participants) {
			player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));
			if(player.interactionManager.getGameMode() == GameMode.SURVIVAL) {

				double theta = ((double) index++ / this.participants.size()) * 2 * Math.PI;

				int x = MathHelper.floor(Math.cos(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));
				int z = MathHelper.floor(Math.sin(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));

				this.spawnLogic.summonPlayerInCageAt(player, x, z);
			}
		}
	}

	private void dropPlayers() {
		this.spawnLogic.clearCages();
		this.gameLogic.setRule(GameRule.BREAK_BLOCKS, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.PLACE_BLOCKS, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
		this.gameLogic.setRule(GameRule.CRAFTING, RuleResult.ALLOW);

		this.participants.forEach(player -> {
			if(player.interactionManager.getGameMode() == GameMode.SURVIVAL) {
				this.spawnLogic.resetPlayer(player, GameMode.SURVIVAL, false);
				this.spawnLogic.applyEffects(player, (int) this.deathmatchEndTick);
			}
		});

		this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.dropped_players").formatted(Formatting.AQUA));
	}

	private void addPlayer(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(this.gameSpace.getWorld().getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));
		this.setSpectator(player, true);
	}

	private void removePlayer(ServerPlayerEntity player) {
		if(player.interactionManager.getGameMode() == GameMode.SURVIVAL) {
			PlayerSet players = this.gameSpace.getPlayers();
			players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.player_eliminated", player.getDisplayName()).formatted(Formatting.BOLD, Formatting.DARK_RED)).append(new LiteralText("\n")));
			players.sendSound(SoundEvents.ENTITY_WITHER_SPAWN);
			this.eliminatePlayer(player);
		}
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		PlayerSet players = this.gameSpace.getPlayers();
		players.sendMessage(new LiteralText("\n").append(source.getDeathMessage(player).copy().formatted(Formatting.BOLD, Formatting.DARK_RED)).append(new LiteralText("\n")));
		players.sendSound(SoundEvents.ENTITY_WITHER_SPAWN);

		this.eliminatePlayer(player);
		return ActionResult.FAIL;
	}

	private void eliminatePlayer(ServerPlayerEntity player) {
		ItemScatterer.spawn(this.gameSpace.getWorld(), player.getBlockPos(), player.inventory);
		this.setSpectator(player, false);
		this.checkForWinner();
	}

	private void checkForWinner() {
		PlayerSet players = this.gameSpace.getPlayers();

		int survival = 0;
		for(ServerPlayerEntity participant : this.participants) {
			if(participant.interactionManager.getGameMode() == GameMode.SURVIVAL) {
				survival++;
			}
		}

		if(survival <= 1) {
			if(survival == 1) {
				for(ServerPlayerEntity participant : this.participants) {
					if(participant.interactionManager.getGameMode() == GameMode.SURVIVAL) {
						players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.player_win", participant.getEntityName()).formatted(Formatting.GOLD)).append("\n"));
						participant.setGameMode(GameMode.ADVENTURE);
						setInvulnerable(true);
						break;
					}
				}
			}
			else {
				players.sendMessage(new LiteralText("\n").append(new TranslatableText("text.uhc.none_win").formatted(Formatting.GOLD)).append("\n"));
			}
			players.sendSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
			this.gameCloseTick = this.gameSpace.getWorld().getTime() + 200;
			this.bar.end();
			this.isFinished = true;
		}
	}

	private void setSpectator(ServerPlayerEntity player, boolean moveToCenter) {
		this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR, true);
		if(moveToCenter) this.spawnLogic.spawnPlayerAtCenter(player);
	}

	private void sendModuleListToChat() {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			MutableText text = new LiteralText("\n").append(new TranslatableText("text.uhc.modules_enabled").formatted(Formatting.GOLD));
			this.modulePieceManager.getModules().forEach(module -> {
				Style style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText(module.getDescription())));
				text.append(new LiteralText("\n  - ").formatted(Formatting.WHITE)).append(Texts.bracketed(new TranslatableText(module.getTranslation()).formatted(Formatting.GREEN)).setStyle(style));
			});
			text.append("\n");
			this.gameSpace.getPlayers().sendMessage(text);
			this.gameSpace.getPlayers().sendSound(SoundEvents.ENTITY_ITEM_PICKUP);
		}
	}

	private ActionResult onPlayerDamage(ServerPlayerEntity entity, DamageSource damageSource, float v) {
		if(this.invulnerable) {
			return ActionResult.FAIL;
		}
		else {
			return ActionResult.SUCCESS;
		}
	}

	public boolean breakIndividualBlock(@Nullable ServerPlayerEntity player, BlockPos pos) {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			for(BlockLootModulePiece piece : this.modulePieceManager.blockLootModulePieces) {
				if(piece.breakBlock(this, player, pos)) return true;
			}
		}
		return false;
	}

	public ActionResult onBlockBroken(@Nullable ServerPlayerEntity player, BlockPos origin) {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			for(BucketBreakModulePiece piece : this.modulePieceManager.bucketBreakModulePieces) {
				if(piece.breakBlock(this, player, origin)) return ActionResult.PASS;
			}
			if(breakIndividualBlock(player, origin)) return ActionResult.PASS;
		}
		return ActionResult.SUCCESS;
	}

	private void onExplosion(List<BlockPos> positions) {
		positions.forEach(pos -> onBlockBroken(null, pos));
	}

	private TypedActionResult<List<ItemStack>> onMobLoot(LivingEntity livingEntity, List<ItemStack> itemStacks) {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			boolean replaceDrops = false;
			List<ItemStack> stacks = new ArrayList<>();
			for(EntityLootModulePiece piece : this.modulePieceManager.entityLootModulePieces) {
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
		}
		return TypedActionResult.success(itemStacks);
	}
}
