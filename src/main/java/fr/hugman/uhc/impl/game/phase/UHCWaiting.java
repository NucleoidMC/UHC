package fr.hugman.uhc.impl.game.phase;

import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.impl.game.ModuleManager;
import fr.hugman.uhc.impl.game.UHCSpawner;
import fr.hugman.uhc.impl.game.ui.element.ModulesUiElement;
import fr.hugman.uhc.impl.map.UHCMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.api.game.common.ui.WaitingLobbyUiLayout;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.event.GameWaitingLobbyEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public record UHCWaiting(
        GameSpace gameSpace,
        ServerLevel level,
        UHCGameConfig config,
        TeamManager teamManager
) {
    public static GameOpenProcedure open(GameOpenContext<UHCGameConfig> context) {
        var config = context.config();
        var registries = context.server().registryAccess();
        var map = UHCMap.of(config, registries);
        var moduleManager = new ModuleManager(config.uhcConfig().value().modules());

        return context.openWithLevel(map.createRuntimeLevelConfig(), (activity, level) -> {
            GameWaitingLobby.addTo(activity, config.players());
            TeamManager teamManager = TeamManager.addTo(activity);

            var gameSpace = activity.getGameSpace();
            UHCWaiting waiting = new UHCWaiting(gameSpace, level, config, teamManager);

            gameSpace.setAttachment(ModuleManager.ATTACHMENT, moduleManager);

            activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
            activity.listen(GamePlayerEvents.ACCEPT, waiting::acceptPlayer);
            activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
            activity.listen(PlayerDeathEvent.EVENT, (player, source) -> EventResult.DENY);
            activity.listen(PlayerDamageEvent.EVENT, (player, source, amount) -> EventResult.DENY);
            activity.listen(PlayerAttackEntityEvent.EVENT, (attacker, hand, attacked, hitResult) -> EventResult.DENY);
            activity.listen(GameWaitingLobbyEvents.BUILD_UI_LAYOUT, waiting::onBuildUiLayout);
        });
    }

    private JoinAcceptorResult acceptPlayer(JoinAcceptor joinAcceptor) {
        return joinAcceptor
                .teleport(this.level, UHCSpawner.getSurfaceBlock(level, 0, 0))
                .thenRunForEach(player -> player.setGameMode(GameType.ADVENTURE));
    }

    private void onBuildUiLayout(WaitingLobbyUiLayout layout, ServerPlayer player) {
        layout.addLeading(new ModulesUiElement(player));
    }

    private GameResult requestStart() {
        UHCActive.start(this.gameSpace, this.level, this.config);
        return GameResult.ok();
    }
}
