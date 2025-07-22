package fr.hugman.lucky_block.api.datagen.provider;

import fr.hugman.lucky_block.api.block.LuckyBlocks;
import fr.hugman.lucky_block.impl.LuckyBlockMod;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;

/**
 * @author Hugman
 * @since 1.0.0
 */
public class LuckyBlockModelProvider extends FabricModelProvider {
    public LuckyBlockModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator gen) {
        registerLuckyBlock(gen, LuckyBlocks.LUCKY_BLOCK, "yellow");

        registerLuckyBlock(gen, LuckyBlocks.SUPER_LUCKY_BLOCK, "green");
        registerLuckyBlock(gen, LuckyBlocks.VERY_LUCKY_BLOCK, "diamond");
        registerLuckyBlock(gen, LuckyBlocks.UNLUCKY_BLOCK, "red");
        registerLuckyBlock(gen, LuckyBlocks.VERY_UNLUCKY_BLOCK, "purple");

        registerLuckyBlock(gen, LuckyBlocks.DOUBLE_LUCKY_BLOCK, "double_yellow", "yellow");
        registerLuckyBlock(gen, LuckyBlocks.TRIPLE_LUCKY_BLOCK, "triple_yellow", "yellow");
    }

    private void registerLuckyBlock(BlockStateModelGenerator gen, Block block, String suffix) {
        registerLuckyBlock(gen, block, suffix, suffix);
    }

    private void registerLuckyBlock(BlockStateModelGenerator gen, Block block, String sideSuffix, String endSuffix) {
        TextureMap textureMap = TextureMap.sideEnd(
                LuckyBlockMod.id("block/lucky_block/side_" + sideSuffix),
                LuckyBlockMod.id("block/lucky_block/end_" + endSuffix)
        );
        var model = Models.CUBE_COLUMN.upload(block, textureMap, gen.modelCollector);
        BlockStateModelGenerator.createWeightedVariant(model);
        gen.itemModelOutput.accept(block.asItem(), ItemModels.basic(model));
    }

    @Override
    public void generateItemModels(ItemModelGenerator gen) {
    }
}

