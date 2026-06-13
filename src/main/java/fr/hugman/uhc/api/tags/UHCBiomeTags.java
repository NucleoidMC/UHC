package fr.hugman.uhc.api.tags;

import fr.hugman.uhc.impl.UHC;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class UHCBiomeTags {
    public static final TagKey<Biome> OCEANLESS_BLACKLIST = create("oceanless_blacklist");

    private static TagKey<Biome> create(final String name) {
        return TagKey.create(Registries.BIOME, UHC.id(name));
    }

}
