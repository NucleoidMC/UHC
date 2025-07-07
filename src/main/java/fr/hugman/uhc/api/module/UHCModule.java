package fr.hugman.uhc.api.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Util;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record UHCModule(
        Text name,
        Optional<Text> description,
        Optional<List<Text>> longDescription,
        ItemStack icon,
        TextColor color,
        List<Modifier> modifiers
) {
    public static final Codec<UHCModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlasmidCodecs.TEXT.fieldOf("name").forGetter(UHCModule::name),
            PlasmidCodecs.TEXT.optionalFieldOf("description").forGetter(UHCModule::description),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("long_description").forGetter(UHCModule::longDescription),
            MoreCodecs.ITEM_STACK.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(UHCModule::icon),
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(3791743)).forGetter(UHCModule::color),
            Modifier.TYPE_CODEC.listOf().fieldOf("modifiers").forGetter(UHCModule::modifiers)
    ).apply(instance, UHCModule::new));

    public static final Codec<RegistryEntry<UHCModule>> ENTRY_CODEC = RegistryElementCodec.of(UHCRegistryKeys.UHC_MODULE, CODEC);
    public static final Codec<RegistryEntryList<UHCModule>> ENTRY_LIST_CODEC = RegistryCodecs.entryList(UHCRegistryKeys.UHC_MODULE, CODEC);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Optional<Text> name;
        private Optional<Text> description = Optional.empty();
        private Optional<List<Text>> longDescription = Optional.empty();
        private ItemStack icon = new ItemStack(Items.BARRIER);
        private TextColor color = TextColor.fromRgb(3791743);
        private List<Modifier> modifiers = List.of();

        private Builder() {}

        public Builder name(Text name) {
            this.name = Optional.of(name);
            return this;
        }

        public Builder nameFrom(RegistryKey<?> key) {
            return name(Text.translatable(Util.createTranslationKey("module", key.getValue())));
        }

        public Builder description(Text description) {
            this.description = Optional.of(description);
            return this;
        }

        public Builder descriptionFrom(RegistryKey<?> key) {
            return description(Text.translatable(Util.createTranslationKey("module", key.getValue()) + ".description"));
        }

        public Builder longDescription(List<Text> longDescription) {
            this.longDescription = Optional.of(longDescription);
            return this;
        }

        public Builder longDescription(Text... longDescription) {
            return longDescription(List.of(longDescription));
        }

        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(ItemConvertible icon) {
            this.icon = new ItemStack(icon);
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

        public Builder applyDisplay(RegistryKey<?> key) {
            var translationKey = Util.createTranslationKey("module", key.getValue());
            if (name.isEmpty()) {
                name(Text.translatable(translationKey));
            }
            if (description.isEmpty()) {
                description(Text.translatable(translationKey + ".description"));
            }
            return this;
        }

        public Builder applyDisplay(RegistryKey<?> key, String... longDescriptionStrings) {
            var translationKey = Util.createTranslationKey("module", key.getValue());
            applyDisplay(key);
            if (longDescription.isEmpty() || longDescription.get().isEmpty()) {
                longDescription(longDescription.orElse(Arrays.stream(longDescriptionStrings)
                        .map(s -> Text.translatable(translationKey + ".description." + s))
                        .collect(Collectors.toUnmodifiableList())));
            }

            return this;
        }

        public UHCModule build() {
            return new UHCModule(name.orElseThrow(), description, longDescription, icon, color, modifiers);
        }
    }
}
