package fr.hugman.uhc.api.portal.menu;

import fr.hugman.uhc.api.config.UHCConfigs;
import fr.hugman.uhc.api.gui.creator.CreateUHCGui;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntry;

import java.util.List;

public class UHCCreatorMenuEntry implements MenuEntry {
    @Override
    public Component name() {
        return Component.translatable("game.custom_uhc");
    }

    @Override
    public List<Component> description() {
        return List.of();
    }

    @Override
    public ItemStack icon() {
        return new ItemStack(Items.COMPASS);
    }

    @Override
    public void click(ServerPlayer player, boolean alt) {
        var config = player.level().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_CONFIG).getOrThrow(UHCConfigs.STANDARD_UHC);
        var ui = new CreateUHCGui(player, config.value().clone());
        ui.open();
    }
}
