package fr.hugman.uhc.impl.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.Stream;

public class LowerSeaOverworldNoiseSettings {
    public static NoiseGeneratorSettings get(BootstrapContext<?> context, boolean isAmplified, boolean largeBiomes) {
        return new NoiseGeneratorSettings(
                NoiseSettings.OVERWORLD_NOISE_SETTINGS,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                noiseRouter(
                        context.lookup(Registries.DENSITY_FUNCTION),
                        context.lookup(Registries.NOISE),
                        largeBiomes,
                        isAmplified
                ),
                SurfaceRuleData.overworld(context.lookup(Registries.BIOME)),
                (new OverworldBiomeBuilder()).spawnTarget(),
                35,
                false,
                true,
                true,
                false
        );
    }

    public static NoiseRouter noiseRouter(
            HolderGetter<DensityFunction> functions,
            HolderGetter<NormalNoise.NoiseParameters> noises,
            boolean largeBiomes,
            boolean amplified
    ) {
        DensityFunction barrierNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_BARRIER), 0.5F);
        DensityFunction fluidLevelFloodednessNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction fluidLevelSpreadNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction lavaNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction shiftX = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = NoiseRouterData.getFunction(functions, NoiseRouterData.SHIFT_Z);
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25F, noises.getOrThrow(largeBiomes ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25F, noises.getOrThrow(largeBiomes ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction offset = NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.OFFSET_LARGE : (amplified ? NoiseRouterData.OFFSET_AMPLIFIED : NoiseRouterData.OFFSET));
        DensityFunction factor = NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.FACTOR_LARGE : (amplified ? NoiseRouterData.FACTOR_AMPLIFIED : NoiseRouterData.FACTOR));
        DensityFunction depth = NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.DEPTH_LARGE : (amplified ? NoiseRouterData.DEPTH_AMPLIFIED : NoiseRouterData.DEPTH));
        DensityFunction preliminarySurfaceLevel = NoiseRouterData.preliminarySurfaceLevel(offset, factor, amplified);
        DensityFunction slopedCheese = NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.SLOPED_CHEESE_LARGE : (amplified ? NoiseRouterData.SLOPED_CHEESE_AMPLIFIED : NoiseRouterData.SLOPED_CHEESE));
        DensityFunction surfaceWithEntrances = DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0F), NoiseRouterData.getFunction(functions, NoiseRouterData.ENTRANCES)));
        DensityFunction caves = DensityFunctions.rangeChoice(slopedCheese, -1000000.0F, 1.5625F, surfaceWithEntrances, NoiseRouterData.underground(functions, noises, slopedCheese));
        DensityFunction fullNoise = DensityFunctions.min(
                NoiseRouterData.postProcess(
                        NoiseRouterData.slide(
                                caves,
                                -64,
                                384,
                                amplified ? 16 : 80,
                                amplified ? 0 : 64,
                                -0.178125,
                                0,
                                24,
                                amplified ? 0.4 : 0.1171875
                        )
                ),
                NoiseRouterData.getFunction(functions, NoiseRouterData.NOODLE)
        );
        DensityFunction y = NoiseRouterData.getFunction(functions, NoiseRouterData.Y);
        int veinMinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((t) -> t.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int veinMaxY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((t) -> t.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction veinToggle = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEININESS), 1.5F, 1.5F), veinMinY, veinMaxY, 0);
        float oreRidgeFrequency = 4.0F;
        DensityFunction veinA = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEIN_A), oreRidgeFrequency, oreRidgeFrequency), veinMinY, veinMaxY, 0).abs();
        DensityFunction veinB = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEIN_B), oreRidgeFrequency, oreRidgeFrequency), veinMinY, veinMaxY, 0).abs();
        DensityFunction veinRidged = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(veinA, veinB));
        DensityFunction veinGap = DensityFunctions.noise(noises.getOrThrow(Noises.ORE_GAP));

        return new NoiseRouter(
                barrierNoise,
                fluidLevelFloodednessNoise,
                fluidLevelSpreadNoise,
                lavaNoise,
                temperature,
                vegetation,
                NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.CONTINENTS_LARGE : NoiseRouterData.CONTINENTS).abs(), // abs() to make sure that the continents are always positive
                NoiseRouterData.getFunction(functions, largeBiomes ? NoiseRouterData.EROSION_LARGE : NoiseRouterData.EROSION),
                depth,
                NoiseRouterData.getFunction(functions, NoiseRouterData.RIDGES),
                preliminarySurfaceLevel,
                fullNoise,
                veinToggle,
                veinRidged,
                veinGap
        );
    }
}
