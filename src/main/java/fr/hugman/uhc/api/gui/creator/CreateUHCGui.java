package fr.hugman.uhc.api.gui.creator;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.game.UHCGameTypes;
import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.impl.portal.game.NewGamePortalBackend;

public class CreateUHCGui extends PreviousableGui {
    private static final int HEIGHT = 3;

    private final UHCConfig config;

    public CreateUHCGui(ServerPlayer player, UHCConfig config) {
        super(BuiltInRegistries.MENU.getValue(Identifier.parse("generic_9x" + HEIGHT)), player, false);
        this.config = config;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        this.displayMainPage();
    }

    private void displayMainPage() {
        int middleRow = (HEIGHT - 1) * 9 / 2;
        clearSlots();
        setTitle(Component.translatable("ui.uhc.create_uhc.title"));
        addBackButton();
        setSlot(8, UHCConfigGuiElements.launch()
                .setCallback((index, type, action, gui) -> {
                    UHCConfigGuiElements.playClickSound(player);
                    displaySelectTeamSizePage();
                }));
        setSlot(middleRow + 2, UHCConfigGuiElements.map(player, config, false));
        setSlot(middleRow + 4, UHCConfigGuiElements.timers(player, config, false));
        setSlot(middleRow + 6, UHCConfigGuiElements.modules(player, config, true));
    }

    private void displaySelectTeamSizePage() {
        setTitle(Component.translatable("ui.uhc.select_team_size.title"));
        clearSlots();
        setSlot(0, UHCConfigGuiElements.back(player, this::displayMainPage));
        setSlot(9 + 1, createTeamSizeElement(UHCGameTeamSize.SOLO));
        setSlot(9 + 3, createTeamSizeElement(UHCGameTeamSize.DUOS));
        setSlot(9 + 5, createTeamSizeElement(UHCGameTeamSize.TRIOS));
        setSlot(9 + 7, createTeamSizeElement(UHCGameTeamSize.SQUADS));
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            setSlot(9 * 2 + 8, createTestingElement());

        }
    }

    private GuiElementBuilder createTeamSizeElement(UHCGameTeamSize teamSize) {
        return teamSize.createElement()
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> player.level().getServer().execute(() -> {
                    UHCConfigGuiElements.playClickSound(player);
                    var gamePortal = new NewGamePortalBackend(Holder.direct(new GameConfig<>(
                            UHCGameTypes.STANDARD,
                            Component.translatable("game.generic.mode", Component.translatable("game.custom_uhc"), Component.translatable("mode." + teamSize.getName())),
                            null,
                            null,
                            new ItemStackTemplate(Items.APPLE),
                            CustomValuesConfig.empty(),
                            new UHCGameConfig(
                                    new WaitingLobbyConfig(new PlayerLimiterConfig(), teamSize.getMinPlayers(), teamSize.getThresholdPlayers(), WaitingLobbyConfig.Countdown.DEFAULT),
                                    teamSize.getTeamsize(),
                                    Holder.direct(config)
                            )
                    )));
                    gamePortal.applyTo(player, false);
                }));
    }

    private GuiElementBuilder createTestingElement() {
        return new GuiElementBuilder(Items.NAUTILUS_SHELL)
                .setName(Component.literal("DEBUG TEST"))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> player.level().getServer().execute(() -> {
                    UHCConfigGuiElements.playClickSound(player);
                    var gamePortal = new NewGamePortalBackend(Holder.direct(new GameConfig<>(
                            UHCGameTypes.STANDARD,
                            Component.translatable("game.generic.mode", Component.translatable("game.custom_uhc"), Component.literal("DEBUG TEST")),
                            null,
                            null,
                            new ItemStackTemplate(Items.APPLE),
                            CustomValuesConfig.empty(),
                            new UHCGameConfig(
                                    new WaitingLobbyConfig(new PlayerLimiterConfig(), 1, 1, WaitingLobbyConfig.Countdown.DEFAULT), 1, Holder.direct(config)
                            )
                    )));
                    gamePortal.applyTo(player, false);
                }));
    }

    private void clearSlots() {
        for (int i = 0; i < HEIGHT * 9; i++) {
            setSlot(i, ItemStack.EMPTY);
        }
    }
}
