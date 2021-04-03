package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.map.UHCMap;
import com.hugman.uhc.game.map.UHCMapGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.fantasy.BubbleWorldSpawner;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.OfferPlayerListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import xyz.nucleoid.plasmid.game.player.JoinResult;

public class UHCWaiting {
	private final GameSpace gameSpace;
	private final UHCMap map;
	private final UHCConfig config;

	private UHCWaiting(GameSpace gameSpace, UHCMap map, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
		UHCMapGenerator generator = new UHCMapGenerator(context.getConfig().mapConfig);
		UHCMap map = generator.build();

		BubbleWorldConfig worldConfig = new BubbleWorldConfig()
				.setGenerator(map.getChunkGenerator(context.getServer()))
				.setSpawner(BubbleWorldSpawner.atSurface(0, 0))
				.setDefaultGameMode(GameMode.SPECTATOR)
				.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, context.getConfig().mapConfig.dimension));

		return context.createOpenProcedure(worldConfig, game -> {
			UHCWaiting waiting = new UHCWaiting(game.getSpace(), map, context.getConfig());

			GameWaitingLobby.applyTo(game, context.getConfig().playerConfig);

			game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
			game.on(OfferPlayerListener.EVENT, waiting::offerPlayer);
			game.on(RequestStartListener.EVENT, waiting::requestStart);
		});
	}

	private boolean isFull() {
		return this.gameSpace.getPlayerCount() >= this.config.playerConfig.getMaxPlayers();
	}

	private JoinResult offerPlayer(ServerPlayerEntity player) {
		return this.isFull() ? JoinResult.gameFull() : JoinResult.ok();
	}

	private StartResult requestStart() {
		UHCActive.open(this.gameSpace, this.map, this.config);
		return StartResult.OK;
	}

	private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		return ActionResult.FAIL;
	}
}
