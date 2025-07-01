package fr.hugman.uhc.api.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.api.modifier.Modifier;
import fr.hugman.uhc.api.registry.UHCRegistryKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

}
