package fr.hugman.uhc.api.command.argument;

import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;

public class UHCConfigArgument extends RegistryEntryArgumentType<UHCConfig> {
    public UHCConfigArgument(CommandRegistryAccess registryAccess) {
        super(registryAccess, UHCRegistryKeys.UHC_CONFIG, UHCConfig.CODEC);
    }
}