package com.hugman.uhc.game.phase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.game.UHCParticipant;
import com.hugman.uhc.game.UHCSpawner;
import com.hugman.uhc.game.UHCTeam;
import com.hugman.uhc.game.map.UHCMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
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
import xyz.nucleoid.plasmid.game.common.team.TeamAllocator;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UHCWaiting {
	public final Object2ObjectMap<ServerPlayerEntity, UHCParticipant> participants;
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final UHCMap map;
	private final UHCConfig config;

	private UHCWaiting(GameSpace gameSpace, ServerWorld world, UHCMap map, UHCConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.config = config;
		this.participants = new Object2ObjectOpenHashMap<>();
	}

	public static GameOpenProcedure open(GameOpenContext<UHCConfig> context) {
		UHCMap map = new UHCMap(context.config(), context.server());

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.getChunkGenerator())
				.setGameRule(GameRules.NATURAL_REGENERATION, false)
				.setGameRule(GameRules.DO_MOB_SPAWNING, true)
				.setGameRule(GameRules.DO_DAYLIGHT_CYCLE, true)
				.setDimensionType(RegistryKey.of(Registry.DIMENSION_TYPE_KEY, context.config().getMapConfig().dimension()));

		return context.openWithWorld(worldConfig, (game, world) -> {
			UHCWaiting waiting = new UHCWaiting(game.getGameSpace(), world, map, context.config());
			GameWaitingLobby.applyTo(game, context.config().getPlayerConfig());

			game.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
			game.listen(GamePlayerEvents.REMOVE, waiting::removePlayer);

			game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
			game.listen(PlayerDeathEvent.EVENT, (player, source) -> ActionResult.FAIL);
			game.listen(PlayerDamageEvent.EVENT, (player, source, amount) -> ActionResult.FAIL);
			game.listen(PlayerAttackEntityEvent.EVENT, (attacker, hand, attacked, hitResult) -> ActionResult.FAIL);
		});
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		UHCParticipant participant = new UHCParticipant();
		participants.put(offer.player(), participant);

		return offer.accept(this.world, UHCSpawner.getSurfaceBlock(world, 0, 0))
				.and(() -> {
					ServerPlayerEntity player = offer.player();

					player.changeGameMode(GameMode.ADVENTURE);
				});
	}

	private void removePlayer(ServerPlayerEntity player) {
		participants.remove(player);
	}

	private GameResult requestStart() {
		HashSet<UHCTeam> teams = new HashSet<>();
		ServerScoreboard scoreboard = gameSpace.getServer().getScoreboard();

		List<DyeColor> teamColors = Arrays.stream(DyeColor.values()).collect(Collectors.toList());
		teamColors.remove(DyeColor.WHITE);
		teamColors.remove(DyeColor.BLACK);
		teamColors.remove(DyeColor.MAGENTA);
		Collections.shuffle(teamColors);

		for(int i = 0; i < Math.round(gameSpace.getPlayers().size() / (float) config.getTeamSize()); i++) {
			Team team = scoreboard.addTeam(RandomStringUtils.randomAlphabetic(16));
			UHCTeam uhcTeam = new UHCTeam(team, teamColors.get(i));

			team.setFriendlyFireAllowed(false);
			team.setShowFriendlyInvisibles(true);
			team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);
			team.setDisplayName(new LiteralText("Team"));
			if(config.getTeamSize() > 1) team.setColor(uhcTeam.getFormatting());

			teams.add(uhcTeam);
		}

		TeamAllocator<UHCTeam, ServerPlayerEntity> allocator = new TeamAllocator<>(teams);

		for(ServerPlayerEntity playerEntity : gameSpace.getPlayers()) {
			allocator.add(playerEntity, null);
		}

		Multimap<UHCTeam, ServerPlayerEntity> teamPlayers = HashMultimap.create();
		allocator.allocate((uhcTeam, player) -> {
			scoreboard.addPlayerToTeam(player.getEntityName(), uhcTeam.getTeam());
			teamPlayers.put(uhcTeam, player);
		});

		UHCActive.start(this.gameSpace, this.world, this.config, this.map, this.participants, teamPlayers);
		return GameResult.ok();
	}
}
