package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.map.UHCMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public record UHCWaiting(GameSpace gameSpace, ServerWorld world, UHCMap map, UHCConfig config, TeamManager teamManager) {
	public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
		UHCMap map = new UHCMap(context.config(), context.server());

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.getChunkGenerator())
				.setGameRule(GameRules.NATURAL_REGENERATION, false)
				.setGameRule(GameRules.DO_MOB_SPAWNING, true)
				.setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
				.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, context.config().mapConfig().dimension()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			GameWaitingLobby.addTo(activity, context.config().playerConfig());
			TeamManager teamManager = TeamManager.addTo(activity);

			UHCWaiting waiting = new UHCWaiting(activity.getGameSpace(), world, map, context.config(), teamManager);

			activity.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
			activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
			activity.listen(PlayerDeathEvent.EVENT, (player, source) -> ActionResult.FAIL);
			activity.listen(PlayerDamageEvent.EVENT, (player, source, amount) -> ActionResult.FAIL);
			activity.listen(PlayerAttackEntityEvent.EVENT, (attacker, hand, attacked, hitResult) -> ActionResult.FAIL);
		});
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.world, UHCSpawner.getSurfaceBlock(world, 0, 0)).and(() -> {
			ServerPlayerEntity player = offer.player();
			player.changeGameMode(GameMode.ADVENTURE);
		});
	}

	private GameResult requestStart() {
		UHCActive.start(this.gameSpace, this.world, this.config, this.map);
		return GameResult.ok();
	}
}
