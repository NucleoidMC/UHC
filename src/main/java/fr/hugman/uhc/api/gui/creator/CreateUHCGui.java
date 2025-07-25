package fr.hugman.uhc.api.gui.creator;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.config.UHCGameConfig;
import fr.hugman.uhc.api.game.UHCGameTeamSize;
import fr.hugman.uhc.api.game.UHCGameTypes;
import fr.hugman.uhc.api.gui.PreviousableGui;
import fr.hugman.uhc.api.gui.UHCConfigGuiElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.common.config.PlayerLimiterConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.impl.portal.game.NewGamePortalBackend;

public class CreateUHCGui extends PreviousableGui {
    private static final int HEIGHT = 3;

    private final UHCConfig config;

    public CreateUHCGui(ServerPlayerEntity player, UHCConfig config) {
        super(Registries.SCREEN_HANDLER.get(Identifier.of("generic_9x" + HEIGHT)), player, false);
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
        setTitle(Text.translatable("ui.uhc.create_uhc.title"));
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
        setTitle(Text.translatable("ui.uhc.select_team_size.title"));
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
                .setCallback((index, type, action, gui) -> player.getServer().execute(() -> {
                    UHCConfigGuiElements.playClickSound(player);
                    var gamePortal = new NewGamePortalBackend(RegistryEntry.of(new GameConfig<>(
                            UHCGameTypes.STANDARD,
                            Text.translatable("game.generic.mode", Text.translatable("game.custom_uhc"), Text.translatable("mode." + teamSize.getName())),
                            null, null, new ItemStack(Items.APPLE), CustomValuesConfig.empty(),
                            new UHCGameConfig(
                                    new WaitingLobbyConfig(new PlayerLimiterConfig(), teamSize.getMinPlayers(), teamSize.getThresholdPlayers(), WaitingLobbyConfig.Countdown.DEFAULT),
                                    teamSize.getTeamsize(),
                                    RegistryEntry.of(config)
                            )
                    )));
                    gamePortal.applyTo(player, false);
                }));
    }

    private GuiElementBuilder createTestingElement() {
        return new GuiElementBuilder(Items.NAUTILUS_SHELL)
                .setName(Text.literal("DEBUG TEST"))
                .hideDefaultTooltip()
                .setCallback((index, type, action, gui) -> player.getServer().execute(() -> {
                    UHCConfigGuiElements.playClickSound(player);
                    var gamePortal = new NewGamePortalBackend(RegistryEntry.of(new GameConfig<>(
                            UHCGameTypes.STANDARD,
                            Text.translatable("game.generic.mode", Text.translatable("game.custom_uhc"), Text.literal("DEBUG TEST")),
                            null, null, new ItemStack(Items.APPLE), CustomValuesConfig.empty(),
                            new UHCGameConfig(
                                    new WaitingLobbyConfig(new PlayerLimiterConfig(), 1, 1, WaitingLobbyConfig.Countdown.DEFAULT), 1, RegistryEntry.of(config)
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
