package fr.hugman.uhc.api.command.argument;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fr.hugman.uhc.api.config.UHCConfig;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class UHCConfigArgument {
    private static final DynamicCommandExceptionType CONFIG_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatableEscape("text.uhc.config.not_found", id));

    public static RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> argument(String name) {
        return Commands.argument(name, ResourceLocationArgument.id()).suggests((ctx, builder) -> {
            Registry<UHCConfig> registry = ctx.getSource().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_CONFIG);
            SharedSuggestionProvider.filterResources(registry.registryKeySet(),
                    builder.getRemaining().toLowerCase(Locale.ROOT),
                    ResourceKey::location,
                    (key) -> registry.get(key)
                            .ifPresent((entry) -> builder.suggest(key.location().toString())));
            return builder.buildFuture();
        });
    }

    public static Holder.Reference<UHCConfig> get(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ResourceKey<UHCConfig> key = ResourceKey.create(UHCRegistryKeys.UHC_CONFIG, ResourceLocationArgument.getId(context, name));
        Registry<UHCConfig> registry = context.getSource().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_CONFIG);
        return registry.get(key).orElseThrow(() -> CONFIG_NOT_FOUND.create(key.location()));
    }
}