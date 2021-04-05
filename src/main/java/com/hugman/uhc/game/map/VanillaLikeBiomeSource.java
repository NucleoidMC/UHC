package com.hugman.uhc.game.map;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;

public class VanillaLikeBiomeSource extends BiomeSource {
   private final BiomeLayerSampler biomeSampler;
   private final Registry<Biome> biomeRegistry;
   private final List<RegistryKey<Biome>> biomes;

   public VanillaLikeBiomeSource(long seed, Registry<Biome> biomeRegistry, List<RegistryKey<Biome>> biomes) {
      super(biomes.stream().map((registryKey) -> () -> (Biome)biomeRegistry.getOrThrow(registryKey)));
      this.biomeRegistry = biomeRegistry;
      this.biomes = biomes;
      this.biomeSampler = BiomeLayers.build(seed, false, 4, 4);
   }

   protected Codec<? extends BiomeSource> getCodec() {
      return CODEC;
   }

   @Environment(EnvType.CLIENT)
   public BiomeSource withSeed(long seed) {
      return new VanillaLikeBiomeSource(seed, this.biomeRegistry, this.biomes);
   }

   public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      return this.biomeSampler.sample(this.biomeRegistry, biomeX, biomeZ);
   }
}
