package fr.hugman.uhc.api.gui.creator;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.game.UHCGameTypes;
import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.impl.portal.game.NewGamePortalBackend;

/**
 * Last stop before a game is opened: recaps what is about to be launched, since the creator's own screen shows each
 * part separately and a player may well have lost track.
 */
public class ConfirmLaunchGui extends PreviousableGui {
    private static final int HEIGHT = 3;

    private final Holder<UHCConfig> preset;
    private final UHCConfig config;
    private final UHCGameTeamSize teamSize;

    public ConfirmLaunchGui(ServerPlayer player, Holder<UHCConfig> preset, UHCConfig config, UHCGameTeamSize teamSize) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
        this.preset = preset;
        this.config = config;
        this.teamSize = teamSize;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        setTitle(Component.translatable("ui.uhc.confirm_launch.title"));
        int middleRow = (HEIGHT - 1) * 9 / 2;

        addBackButton();
        setSlot(middleRow + 2, summary());
        setSlot(middleRow + 6, UHCConfigGuiElements.confirm(player, "ui.uhc.confirm", this::launch));
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            setSlot(HEIGHT * 9 - 1, debug());
        }
    }

    private GuiElementBuilder summary() {
        var element = new GuiElementBuilder(Items.WRITTEN_BOOK)
                .hideDefaultTooltip()
                .setName(Component.translatable("ui.uhc.summary").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));

        element.addLoreLine(Component.empty());
        line(element, "ui.uhc.preset", UHCConfigGuiElements.presetName(preset));
        line(element, "ui.uhc.team_size", teamSize.getDisplayName());
        line(element, "text.uhc.modules", Component.literal(String.valueOf(config.modules().size())));
        line(element, "ui.uhc.min_players.short", Component.literal(String.valueOf(teamSize.getMinPlayers())));
        return element;
    }

    private static void line(GuiElementBuilder element, String key, Component value) {
        element.addLoreLine(Component.translatable("ui.uhc.setting.summary",
                Component.translatable(key), value.copy().withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
    }

    private void launch() {
        open(teamSize.getMinPlayers(), teamSize.getThresholdPlayers());
    }

    private GuiElementBuilder debug() {
        return new GuiElementBuilder(Items.NAUTILUS_SHELL)
                .hideDefaultTooltip()
                .setName(Component.literal("DEBUG TEST"))
                .setCallback((index, type, action, gui) -> {
                    UHCConfigGuiElements.playClickSound(player);
                    open(1, 1);
                });
    }

    private void open(int minPlayers, int thresholdPlayers) {
        player.level().getServer().execute(() -> {
            var gamePortal = new NewGamePortalBackend(Holder.direct(new GameConfig<>(
                    UHCGameTypes.STANDARD,
                    Component.translatable("game.generic.mode",
                            Component.translatable("game.custom_uhc"), teamSize.getDisplayName()),
                    null,
                    null,
                    new ItemStackTemplate(Items.APPLE),
                    CustomValuesConfig.empty(),
                    new UHCGameConfig(
                            new WaitingLobbyConfig(new PlayerLimiterConfig(), minPlayers, thresholdPlayers, WaitingLobbyConfig.Countdown.DEFAULT),
                            teamSize.getTeamsize(),
                            Holder.direct(config)
                    )
            )));
            gamePortal.applyTo(player, false);
        });
    }
}
