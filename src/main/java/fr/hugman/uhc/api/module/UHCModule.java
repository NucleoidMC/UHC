package fr.hugman.uhc.api.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record UHCModule(
        Component name,
        Optional<Component> description,
        Optional<List<Component>> longDescription,
        ItemStackTemplate icon,
        TextColor color,
        List<Modifier> modifiers
) {
    public static final Codec<UHCModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlasmidCodecs.TEXT.fieldOf("name").forGetter(UHCModule::name),
            PlasmidCodecs.TEXT.optionalFieldOf("description").forGetter(UHCModule::description),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("long_description").forGetter(UHCModule::longDescription),
            ItemStackTemplate.CODEC.optionalFieldOf("icon", new ItemStackTemplate(Items.BARRIER)).forGetter(UHCModule::icon),
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(3791743)).forGetter(UHCModule::color),
            Modifier.TYPE_CODEC.listOf().fieldOf("modifiers").forGetter(UHCModule::modifiers)
    ).apply(instance, UHCModule::new));

    public static final Codec<Holder<UHCModule>> ENTRY_CODEC = RegistryFileCodec.create(UHCRegistryKeys.UHC_MODULE, CODEC);
    public static final Codec<HolderSet<UHCModule>> ENTRY_LIST_CODEC = RegistryCodecs.homogeneousList(UHCRegistryKeys.UHC_MODULE, CODEC);

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a {@link GuiElementBuilder} for this module, displaying its icon, name, and description.
     */
    public GuiElementBuilder getElement() {
        GuiElementBuilder element = new GuiElementBuilder(icon)
                .setName(name.copy().withStyle(ChatFormatting.BOLD).setStyle(Style.EMPTY.withColor(color)))
                .hideDefaultTooltip();
        return element;
    }

    public void addDescriptionToElement(GuiElementBuilder element) {
        if (longDescription.isPresent()) {
            element.addLoreLine(Component.literal(""));
            for (Component line : longDescription.get()) {
                element.addLoreLine(Component.literal("- ").append(line));
            }
        } else if (description.isPresent()) {
            element.addLoreLine(Component.literal(""));
            element.addLoreLine(Component.literal("- ").append(description.get()));
        }
    }

    public static class Builder {
        private Optional<Component> name;
        private Optional<Component> description = Optional.empty();
        private Optional<List<Component>> longDescription = Optional.empty();
        private ItemStackTemplate icon = new ItemStackTemplate(Items.BARRIER);
        private TextColor color = TextColor.fromRgb(3791743);
        private List<Modifier> modifiers = List.of();

        private Builder() {
        }

        public Builder name(Component name) {
            this.name = Optional.of(name);
            return this;
        }

        /**
         * Sets a standard name of the module using its registry key.
         */
        public Builder nameFrom(ResourceKey<?> key) {
            return name(Component.translatable(Util.makeDescriptionId("module", key.identifier())));
        }

        public Builder description(Component description) {
            this.description = Optional.of(description);
            return this;
        }

        /**
         * Sets a standard description of the module using its registry key.
         */
        public Builder descriptionFrom(ResourceKey<?> key) {
            return description(Component.translatable(Util.makeDescriptionId("module", key.identifier()) + ".description"));
        }

        public Builder longDescription(List<Component> longDescription) {
            this.longDescription = Optional.of(longDescription);
            return this;
        }

        public Builder longDescription(Component... longDescription) {
            return longDescription(List.of(longDescription));
        }

        /**
         * Sets a standard long description of the module, using the registry key of the module the lines belong to.
         * That key is not necessarily this module's own: variants usually reuse the lines of the module they derive
         * from.
         */
        public Builder longDescriptionFrom(ResourceKey<?> key, String... paths) {
            var translationKey = Util.makeDescriptionId("module", key.identifier());
            return longDescription(Arrays.stream(paths)
                    .map(path -> Component.translatable(translationKey + ".description." + path))
                    .map(Component.class::cast)
                    .toList());
        }

        public Builder icon(ItemStackTemplate icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(ItemLike icon) {
            this.icon = new ItemStackTemplate(icon.asItem());
            return this;
        }

        public Builder color(TextColor color) {
            this.color = color;
            return this;
        }

        public Builder modifiers(List<Modifier> modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public Builder modifiers(Modifier... modifiers) {
            this.modifiers = List.of(modifiers);
            return this;
        }

        public Builder applyDisplay(ResourceKey<?> key) {
            var translationKey = Util.makeDescriptionId("module", key.identifier());
            if (name.isEmpty()) {
                name(Component.translatable(translationKey));
            }
            if (description.isEmpty()) {
                description(Component.translatable(translationKey + ".description"));
            }
            return this;
        }

        public Builder applyDisplay(ResourceKey<?> key, String... longDescriptionStrings) {
            var translationKey = Util.makeDescriptionId("module", key.identifier());
            applyDisplay(key);
            if (longDescription.isEmpty() || longDescription.get().isEmpty()) {
                longDescription(longDescription.orElse(Arrays.stream(longDescriptionStrings)
                        .map(s -> Component.translatable(translationKey + ".description." + s))
                        .collect(Collectors.toUnmodifiableList())));
            }

            return this;
        }

        public UHCModule build() {
            return new UHCModule(name.orElseThrow(), description, longDescription, icon, color, modifiers);
        }
    }
}
