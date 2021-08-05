package com.hugman.uhc.game.phase;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCParticipant;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.map.UHCMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.TeamAllocator;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record UHCWaiting(GameSpace gameSpace, TeamManager teamManager, ServerWorld world, UHCMap map, UHCConfig config) {
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

			UHCWaiting waiting = new UHCWaiting(activity.getGameSpace(), teamManager, world, map, context.config());

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
		List<GameTeam> teams = new ArrayList<>();

		List<DyeColor> teamColors = Arrays.stream(DyeColor.values()).collect(Collectors.toList());
		teamColors.remove(DyeColor.WHITE);
		teamColors.remove(DyeColor.BLACK);
		teamColors.remove(DyeColor.MAGENTA);
		Collections.shuffle(teamColors);

		for(int i = 0; i < Math.round(gameSpace.getPlayers().size() / (float) config.teamSize()); i++) {
			GameTeam gameTeam = new GameTeam(RandomStringUtils.randomAlphabetic(16), new LiteralText("UHC Team"), GameTeam.Colors.from(teamColors.get(i)));

			teamManager.addTeam(gameTeam);
			teamManager.setFriendlyFire(gameTeam, false);
			teamManager.setCollisionRule(gameTeam, AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);
			teams.add(gameTeam);
		}

		TeamAllocator<GameTeam, ServerPlayerEntity> allocator = new TeamAllocator<>(teams);
		Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants = new Object2ObjectOpenHashMap<>();
		for(ServerPlayerEntity playerEntity : gameSpace.getPlayers()) {
			allocator.add(playerEntity, null);
			UHCParticipant participant = new UHCParticipant();
			participants.put(playerEntity, participant);
		}
		allocator.allocate((gameTeam, playerEntity) -> teamManager.addPlayerTo(playerEntity, gameTeam));

		UHCActive.start(this.gameSpace, this.world, this.config, this.map, participants, this.teamManager, teams);
		return GameResult.ok();
	}
}
