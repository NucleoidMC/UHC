package fr.hugman.uhc.api.command.argument;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fr.hugman.uhc.api.module.UHCModule;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.impl.game.ModuleManager;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public final class UHCModuleArgument {
    private static final DynamicCommandExceptionType MODULE_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatableEscape("text.uhc.module.not_found", id));

    public static RequiredArgumentBuilder<CommandSourceStack, Identifier> argumentFromEnabled(String name) {
        return Commands.argument(name, IdentifierArgument.id()).suggests((ctx, builder) -> {
            Registry<UHCModule> registry = ctx.getSource().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_MODULE);
            String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
            var manager = Objects.requireNonNull(GameSpaceManager.get().byLevel(ctx.getSource().getLevel())).getAttachment(ModuleManager.ATTACHMENT);
            if (manager == null) {
                return builder.buildFuture();
            }
            var enabledKeys = manager.keys();
            SharedSuggestionProvider.filterResources(enabledKeys, remaining, ResourceKey::identifier, (key) -> registry.get(key)
                    .ifPresent((entry) -> builder.suggest(key.identifier().toString(), entry.value().name())));
            return builder.buildFuture();
        });
    }

    public static RequiredArgumentBuilder<CommandSourceStack, Identifier> argumentFromDisabled(String name) {
        return Commands.argument(name, IdentifierArgument.id()).suggests((ctx, builder) -> {
            Registry<UHCModule> registry = ctx.getSource().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_MODULE);
            String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
            var manager = Objects.requireNonNull(GameSpaceManager.get().byLevel(ctx.getSource().getLevel())).getAttachment(ModuleManager.ATTACHMENT);
            var candidates = new ArrayList<>(registry.registryKeySet());
            if (manager != null) {
                candidates.removeAll(manager.keys());
            }
            SharedSuggestionProvider.filterResources(candidates, remaining, ResourceKey::identifier, (key) -> registry.get(key)
                    .ifPresent((entry) -> builder.suggest(key.identifier().toString(), entry.value().name())));
            return builder.buildFuture();
        });
    }

    public static Holder.Reference<UHCModule> get(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ResourceKey<UHCModule> key = ResourceKey.create(UHCRegistryKeys.UHC_MODULE, IdentifierArgument.getId(context, name));
        Registry<UHCModule> registry = context.getSource().registryAccess().lookupOrThrow(UHCRegistryKeys.UHC_MODULE);
        return registry.get(key).orElseThrow(() -> MODULE_NOT_FOUND.create(key.identifier()));
    }
}
