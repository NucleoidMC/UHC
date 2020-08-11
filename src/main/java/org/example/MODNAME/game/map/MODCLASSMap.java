package org.example.MODNAME.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.game.map.template.TemplateChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MODCLASSMap {
    private final MapTemplate template;
    private final MODCLASSMapConfig config;
    public BlockPos spawn;

    public MODCLASSMap(MapTemplate template, MODCLASSMapConfig config) {
        this.template = template;
        this.config = config;
    }

    public ChunkGenerator asGenerator() {
        return new TemplateChunkGenerator(this.template, BlockPos.ORIGIN);
    }
}
