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
    }

    private void registerLuckyBlock(BlockStateModelGenerator gen, Block block, String textureSuffix) {
        TextureMap textureMap = TextureMap.sideEnd(
                LuckyBlockMod.id("block/lucky_block/side_" + textureSuffix),
                LuckyBlockMod.id("block/lucky_block/end_" + textureSuffix)
        );
        var model = Models.CUBE_COLUMN.upload(block, textureMap, gen.modelCollector);
        BlockStateModelGenerator.createWeightedVariant(model);
        gen.itemModelOutput.accept(block.asItem(), ItemModels.basic(model));
    }

    @Override
    public void generateItemModels(ItemModelGenerator gen) {
    }
}

