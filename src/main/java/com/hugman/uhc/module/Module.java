package com.hugman.uhc.module;

import com.hugman.uhc.modifier.Modifier;
import com.hugman.uhc.registry.UHCRegistryKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.List;
import java.util.Optional;

public record Module(
        Text name,
        Optional<Text> description,
        Optional<List<Text>> longDescription,
        ItemStack icon,
        TextColor color,
        List<Modifier> modifiers
) {
    public static final Codec<Module> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlasmidCodecs.TEXT.fieldOf("name").forGetter(Module::name),
            PlasmidCodecs.TEXT.optionalFieldOf("description").forGetter(Module::description),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("long_description").forGetter(Module::longDescription),
            MoreCodecs.ITEM_STACK.optionalFieldOf("icon", new ItemStack(Items.BARRIER)).forGetter(Module::icon),
            TextColor.CODEC.optionalFieldOf("color", TextColor.fromRgb(3791743)).forGetter(Module::color),
            Modifier.TYPE_CODEC.listOf().fieldOf("modifiers").forGetter(Module::modifiers)
    ).apply(instance, Module::new));

    public static final Codec<RegistryEntry<Module>> ENTRY_CODEC = RegistryElementCodec.of(UHCRegistryKeys.MODULE, CODEC);
    public static final Codec<RegistryEntryList<Module>> ENTRY_LIST_CODEC = RegistryCodecs.entryList(UHCRegistryKeys.MODULE, CODEC);

}
