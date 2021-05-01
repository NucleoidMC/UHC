package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCBar;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.UHCLogic;
import com.hugman.uhc.game.map.UHCMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameCloseListener;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.PlayerRemoveListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public class UHCInGame {
	public final GameSpace gameSpace;
	private final UHCMap map;
	private final UHCConfig config;

	private final PlayerSet participants;

	private final UHCLogic logic;
	private final UHCSpawner spawnLogic;
	private final UHCBar bar;

	private long cageDropTime;
	private long shrinkStartTime;
	private boolean borderShrinkStarted = false;
	private long gameCloseTick = Long.MAX_VALUE;
	private boolean finished = false;

	private UHCInGame(GameSpace gameSpace, UHCMap map, UHCConfig config, PlayerSet participants, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;

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

			game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);

			active.config.getModulesPieces().forEach(piece -> piece.init(game));
		});
	}

	private void open() {
		ServerWorld world = this.gameSpace.getWorld();

		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(this.logic.getMapMaxSize());
		world.getWorldBorder().setDamagePerBlock(0.5);
		this.cageDropTime = world.getTime() + this.logic.getInCagesTime();

		int index = 0;

		for(ServerPlayerEntity player : this.participants) {
			player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.INITIALIZE));

			double theta = ((double) index++ / this.participants.size()) * 2 * Math.PI;

			int x = MathHelper.floor(Math.cos(theta) * this.logic.getMapMaxSize() / 2 - 100);
			int z = MathHelper.floor(Math.sin(theta) * this.logic.getMapMaxSize() / 2 - 100);

			this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);

			this.spawnLogic.summonPlayerInCageAt(player, x, z);
		}
		if(!config.getModules().isEmpty()) {
			MutableText text = new TranslatableText("text.uhc.modules_enabled").formatted(Formatting.AQUA);
			config.getModules().forEach(module -> text.append("\n  - ").append(module.getName().formatted(Formatting.GREEN)));
			gameSpace.getPlayers().sendMessage(text);
		}
	}

	private void close() {
		for(ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	private void tick() {
		ServerWorld world = this.gameSpace.getWorld();
		int playerAmount = participants.size();

		// Players are in the cages
		if(world.getTime() < this.cageDropTime) {
			this.bar.tickStarting(this.cageDropTime - world.getTime(), this.logic.getInCagesTime());
		}
		// Players are dropping from the cage
		else if(world.getTime() == this.cageDropTime) {
			this.spawnLogic.clearCages();
			this.participants.forEach(player -> {
				this.spawnLogic.resetPlayer(player, GameMode.SURVIVAL);
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, (int) this.logic.getInvulnerabilityTime(), 127, true, false));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, (int) this.logic.getInvulnerabilityTime(), 127, true, false));
			});
		}
		// Players are in the map, world border is at max
		else if(!this.borderShrinkStarted) {
			long totalSafeTime = this.logic.getSetupTime();
			this.bar.tickSafe(totalSafeTime - (world.getTime() - this.cageDropTime), totalSafeTime);

			if((world.getTime() - this.cageDropTime) > totalSafeTime) {
				this.bar.setActive();
				this.borderShrinkStarted = true;
				this.shrinkStartTime = world.getTime();
				gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.worldborder_started_shrinking").formatted(Formatting.RED));

				world.getWorldBorder().interpolateSize(this.logic.getMapMaxSize(), this.logic.getMapMinSize(), this.logic.getShrinkingTime() * 50L);
				for(ServerPlayerEntity player : gameSpace.getPlayers()) {
					player.networkHandler.sendPacket(new WorldBorderS2CPacket(world.getWorldBorder(), WorldBorderS2CPacket.Type.LERP_SIZE));
				}
			}
		}
		// Players are in the map, world border is shrinking or finished
		else {
			if((world.getTime() - this.shrinkStartTime) > this.logic.getShrinkingTime() || world.getWorldBorder().getSize() == this.logic.getMapMinSize()) {
				if(!this.finished) {
					gameSpace.getPlayers().sendMessage(new TranslatableText("text.uhc.last_one_wins").formatted(Formatting.BLUE));
					world.getWorldBorder().setDamagePerBlock(2.5);
					world.getWorldBorder().setBuffer(0.125);
					this.bar.setFinished();

					this.finished = true;
				}
			}
			else {
				this.bar.tickShrinking(this.logic.getShrinkingTime() - (world.getTime() - this.shrinkStartTime), this.logic.getShrinkingTime());
			}
		}

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
}
