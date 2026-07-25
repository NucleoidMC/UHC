package fr.hugman.uhc.api.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import fr.hugman.uhc.api.util.Sprites;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record UHCModule(
        Component name,
        Optional<Component> description,
        Optional<List<Component>> longDescription,
        ItemStackTemplate icon,
        TextColor color,
        HolderSet<UHCModule> incompatibleWith,
        List<Modifier> modifiers
) {
    public static final Codec<UHCModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlasmidCodecs.TEXT.fieldOf("name").forGetter(UHCModule::name),
            PlasmidCodecs.TEXT.optionalFieldOf("description").forGetter(UHCModule::description),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("long_description").forGetter(UHCModule::longDescription),
            ItemStackTemplate.CODEC.optionalFieldOf("icon", new ItemStackTemplate(Items.BARRIER)).forGetter(UHCModule::icon),
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(3791743)).forGetter(UHCModule::color),
            // References only, since allowing inline modules here would make this codec depend on itself.
            RegistryCodecs.homogeneousList(UHCRegistryKeys.UHC_MODULE).optionalFieldOf("incompatible_with", HolderSet.direct()).forGetter(UHCModule::incompatibleWith),
            Modifier.TYPE_CODEC.listOf().fieldOf("modifiers").forGetter(UHCModule::modifiers)
    ).apply(instance, UHCModule::new));

    public static final Codec<Holder<UHCModule>> ENTRY_CODEC = RegistryFileCodec.create(UHCRegistryKeys.UHC_MODULE, CODEC);
    public static final Codec<HolderSet<UHCModule>> ENTRY_LIST_CODEC = RegistryCodecs.homogeneousList(UHCRegistryKeys.UHC_MODULE, CODEC);

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Whether two modules step on each other and cannot be enabled together, such as a module and its {@code +}
     * variant.
     * <p>
     * A pair only needs to be declared from one side: declaring it from both would make the two module files reference
     * each other, which the registry loader has no reason to enjoy.
     */
    public static boolean areIncompatible(Holder<UHCModule> first, Holder<UHCModule> second) {
        return first.value().incompatibleWith().contains(second) || second.value().incompatibleWith().contains(first);
    }

    /**
     * The first of {@code modules} this one cannot be enabled alongside, if any.
     */
    public static Optional<Holder<UHCModule>> findIncompatibility(Holder<UHCModule> module, Collection<Holder<UHCModule>> modules) {
        return modules.stream().filter(other -> areIncompatible(module, other)).findFirst();
    }

    /**
     * A long description line: the translation path of the sentence, and the sprites illustrating the change it
     * describes.
     */
    public record DescriptionLine(String path, Sprites.Transformation sprites) {
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
                element.addLoreLine(bullet(line));
            }
        } else if (description.isPresent()) {
            element.addLoreLine(Component.literal(""));
            element.addLoreLine(bullet(description.get()));
        }
    }

    /**
     * Lines opening on sprites use them as their bullet, since they already stand out; plain lines get a dash.
     */
    private static Component bullet(Component line) {
        return line.getContents() instanceof ObjectContents ? line : Component.literal("- ").append(line);
    }

    public static class Builder {
        private Optional<Component> name;
        private Optional<Component> description = Optional.empty();
        private Optional<List<Component>> longDescription = Optional.empty();
        private ItemStackTemplate icon = new ItemStackTemplate(Items.BARRIER);
        private TextColor color = TextColor.fromRgb(3791743);
        private HolderSet<UHCModule> incompatibleWith = HolderSet.direct();
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

        /**
         * Sets a standard description of the module, opening on sprites illustrating what it changes.
         */
        public Builder descriptionFrom(ResourceKey<?> key, Sprites.Transformation sprites) {
            return description(illustrate(sprites.render(), Component.translatable(Util.makeDescriptionId("module", key.identifier()) + ".description")));
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

        /**
         * Same as {@link #longDescriptionFrom(ResourceKey, String...)}, each line opening on sprites illustrating the
         * change it describes.
         * <p>
         * Every line is padded to the widest one, so that the arrows and the sentences stay in straight columns however
         * many sprites a given line needs.
         */
        public Builder longDescriptionFrom(ResourceKey<?> key, DescriptionLine... lines) {
            var translationKey = Util.makeDescriptionId("module", key.identifier());
            var fromWidth = Arrays.stream(lines).mapToInt(line -> line.sprites().from().size()).max().orElse(0);
            var toWidth = Arrays.stream(lines).mapToInt(line -> line.sprites().to().size()).max().orElse(0);
            return longDescription(Arrays.stream(lines)
                    .map(line -> illustrate(line.sprites().render(fromWidth, toWidth), Component.translatable(translationKey + ".description." + line.path())))
                    .toList());
        }

        /**
         * Sprites are 8 pixels wide where a space is 4, so a single one would sit too close to the sentence.
         */
        private static Component illustrate(Component sprites, Component text) {
            return sprites.copy().append("  ").append(text);
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

        /**
         * Marks modules this one cannot be enabled alongside. Only one side of each pair needs to declare it.
         */
        @SafeVarargs
        public final Builder incompatibleWith(Holder<UHCModule>... modules) {
            this.incompatibleWith = HolderSet.direct(modules);
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
            return new UHCModule(name.orElseThrow(), description, longDescription, icon, color, incompatibleWith, modifiers);
        }
    }
}
