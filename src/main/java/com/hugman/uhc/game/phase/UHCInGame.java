package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCBar;
import com.hugman.uhc.game.UHCLogic;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.map.UHCMap;
import com.hugman.uhc.module.ModulePieceManager;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.LootReplaceModulePiece;
import com.hugman.uhc.util.BucketScanner;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.util.Collections;
import java.util.List;

public class UHCInGame {
	public final GameSpace gameSpace;
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
	private long shrinkingEndTick;

	private long gameCloseTick = Long.MAX_VALUE;
	private boolean invulnerable;

	private UHCInGame(GameSpace gameSpace, UHCMap map, UHCConfig config, PlayerSet participants, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
		this.modulePieceManager = new ModulePieceManager(config);

		this.participants = participants;

		this.logic = new UHCLogic(config, participants.size());
		this.spawnLogic = new UHCSpawner(gameSpace, config);
		this.bar = UHCBar.create(widgets);
	}

	public static void open(GameSpace gameSpace, UHCMap map, UHCConfig config) {
		gameSpace.openGame(game -> {
			GlobalWidgets widgets = new GlobalWidgets(game);
			UHCInGame active = new UHCInGame(gameSpace, map, config, gameSpace.getPlayers(), widgets);

			game.setRule(GameRule.CRAFTING, RuleResult.ALLOW);
			game.setRule(GameRule.PORTALS, RuleResult.DENY);
			game.setRule(GameRule.PVP, RuleResult.ALLOW);
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
			game.on(BreakBlockListener.EVENT, active::breakBlock);
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
		this.shrinkingEndTick = this.wildEndTick + this.logic.getShrinkingTime();

		this.invulnerable = true;

		int index = 0;

		for(ServerPlayerEntity player : this.participants) {
			player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));

			double theta = ((double) index++ / this.participants.size()) * 2 * Math.PI;

			int x = MathHelper.floor(Math.cos(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));
			int z = MathHelper.floor(Math.sin(theta) * (this.logic.getStartMapSize() / 2 - this.config.getMapConfig().getSpawnOffset()));

			this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);

			this.spawnLogic.summonPlayerInCageAt(player, x, z);
		}
	}

	private void close() {
		for(ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	private void tick() {
		ServerWorld world = this.gameSpace.getWorld();

		// Cage chapter
		if(world.getTime() < this.cagesEndTick) {
			this.bar.tickCages(this.cagesEndTick - world.getTime(), this.logic.getInCagesTime());
			if(world.getTime() == this.cagesEndTick - (logic.getInCagesTime() * 0.8)) {
				this.sendModuleListToChat();
			}
		}
		// Cage chapter ends
		else if(world.getTime() == this.cagesEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.cages_end").formatted(Formatting.AQUA));
			this.spawnLogic.clearCages();
			this.participants.forEach(player -> this.spawnLogic.resetPlayer(player, GameMode.SURVIVAL));
		}
		// Invulnerable chapter
		else if(world.getTime() < this.invulnerabilityEndTick) {
			this.bar.tickInvulnerable(this.invulnerabilityEndTick - world.getTime(), this.logic.getInvulnerabilityTime());
		}
		// Invulnerable chapter ends
		else if(world.getTime() == this.invulnerabilityEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.invulnerability_end").formatted(Formatting.RED));
			this.invulnerable = false;
		}
		// Peaceful chapter
		else if(world.getTime() < this.peacefulEndTick) {
			this.bar.tickPeaceful(this.peacefulEndTick - world.getTime(), this.logic.getPeacefulTime());
		}
		// Peaceful chapter ends
		else if(world.getTime() == this.peacefulEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.peaceful_end").formatted(Formatting.RED));
			this.invulnerable = false;
		}
		// Wild chapter
		else if(world.getTime() < this.wildEndTick) {
			this.bar.tickWild(this.wildEndTick - world.getTime(), this.logic.getWildTime());
		}
		// Wild chapter ends
		else if(world.getTime() == this.wildEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.wild_end").formatted(Formatting.RED));

			world.getWorldBorder().interpolateSize(this.logic.getStartMapSize(), this.logic.getEndMapSize(), this.logic.getShrinkingTime() * 50L);
			for(ServerPlayerEntity player : this.gameSpace.getPlayers()) {
				player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.LERP_SIZE));
			}
		}
		// Shrinking chapter
		else if(world.getTime() < this.shrinkingEndTick) {
			this.bar.tickShrinking(this.logic.getShrinkingTime() - (world.getTime() - this.wildEndTick), this.logic.getShrinkingTime());
		}
		// Shrinking chapter ends
		else if(world.getTime() == this.shrinkingEndTick) {
			this.gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.shrinking_end").formatted(Formatting.AQUA));
			world.getWorldBorder().setDamagePerBlock(2.5);
			world.getWorldBorder().setBuffer(0.125);
			this.bar.setDeathmatch();
		}
		// Game ends
		if(world.getTime() > this.gameCloseTick) {
			this.gameSpace.close(GameCloseReason.FINISHED);
		}
	}

	private void addPlayer(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(this.gameSpace.getWorld().getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));
		this.spawnSpectator(player);
	}

	private void removePlayer(ServerPlayerEntity player) {
		this.eliminatePlayer(player);
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		this.eliminatePlayer(player);
		return ActionResult.SUCCESS;
	}

	private void eliminatePlayer(ServerPlayerEntity player) {
		PlayerSet players = this.gameSpace.getPlayers();
		players.sendMessage(new TranslatableText("text.uhc.player_eliminated", player.getDisplayName()).formatted(Formatting.RED));
		players.sendSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

		ItemScatterer.spawn(this.gameSpace.getWorld(), player.getBlockPos(), player.inventory);

		this.spawnSpectator(player);

		int survival = 0;
		for(ServerPlayerEntity participant : this.participants) {
			if(participant.interactionManager.getGameMode().isSurvivalLike()) {
				survival++;
			}
		}

		if(survival == 1) {
			for(ServerPlayerEntity participant : this.participants) {
				if(participant.interactionManager.getGameMode().isSurvivalLike()) {
					players.sendMessage(new TranslatableText("text.uhc.player_win", participant.getEntityName()).formatted(Formatting.GOLD));
					this.gameCloseTick = this.gameSpace.getWorld().getTime() + (20 * 10);
					break;
				}
			}
		}
	}

	private void spawnSpectator(ServerPlayerEntity player) {
		this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
		this.spawnLogic.spawnPlayerAtCenter(player);
	}

	private void sendModuleListToChat() {
		if(!this.modulePieceManager.getModules().isEmpty()) {
			MutableText text = new TranslatableText("text.uhc.modules_enabled").formatted(Formatting.AQUA);
			this.modulePieceManager.getModules().forEach(module -> text.append("\n  - ").append(module.getName().formatted(Formatting.GREEN)));
			this.gameSpace.getPlayers().sendMessage(text);
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

	public ActionResult breakBlock(@Nullable ServerPlayerEntity player, BlockPos origin) {
		ServerWorld world = this.gameSpace.getWorld();
		BlockState state = world.getBlockState(origin);

		if(!this.modulePieceManager.getModules().isEmpty()) {
			for(BucketBreakModulePiece piece : this.modulePieceManager.bucketBreakModulePieces) {
				if(piece.getPredicate().test(state, world.getRandom())) {
					LongSet positions = BucketScanner.findTwentySix(origin, piece.getDepth(), pos -> world.getWorldBorder().contains(pos) && piece.getPredicate().test(world.getBlockState(pos), world.getRandom()));
					for(long l : positions) {
						if(l != origin.asLong()) {
							BlockPos pos = BlockPos.fromLong(l);
							if(world.getWorldBorder().contains(pos)) {
								if(!breakIndividualBlock(player, pos)) {
									world.breakBlock(pos, true, player);
								}
							}
						}
					}
					if(breakIndividualBlock(player, origin)) return ActionResult.PASS;
				}
			}
			if(breakIndividualBlock(player, origin)) return ActionResult.PASS;
		}
		return ActionResult.SUCCESS;
	}

	private void onExplosion(List<BlockPos> positions) {
		positions.forEach(pos -> breakBlock(null, pos));
	}

	public boolean breakIndividualBlock(@Nullable ServerPlayerEntity player, BlockPos pos) {
		ServerWorld world = this.gameSpace.getWorld();
		BlockState state = world.getBlockState(pos);

		for(LootReplaceModulePiece piece : this.modulePieceManager.lootReplaceModulePieces) {
			if(piece.getPredicate().test(state, world.getRandom())) {
				LootContext.Builder builder = (new LootContext.Builder(world)).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).parameter(LootContextParameters.TOOL, player == null ? ItemStack.EMPTY : player.getMainHandStack()).optionalParameter(LootContextParameters.THIS_ENTITY, player).optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));
				List<ItemStack> stacks;
				if(piece.getLootTable() == LootTables.EMPTY) {
					stacks = Collections.emptyList();
				}
				else {
					LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
					LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getTable(piece.getLootTable());
					stacks = lootTable.generateLoot(lootContext);
				}
				int xp = piece.getExperience();
				while(xp > 0) {
					int i = ExperienceOrbEntity.roundToOrbSize(xp);
					xp -= i;
					world.spawnEntity(new ExperienceOrbEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, i));
				}
				stacks.forEach((stack) -> Block.dropStack(world, pos, stack));
				world.breakBlock(pos, false, player);
				return true;
			}
		}
		return false;
	}
}
