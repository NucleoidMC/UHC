package com.hugman.uhc.module;

import com.hugman.uhc.config.UHCConfig;
import com.hugman.uhc.module.piece.BucketBreakModulePiece;
import com.hugman.uhc.module.piece.LootReplaceModulePiece;
import com.hugman.uhc.module.piece.ModulePiece;
import com.hugman.uhc.module.piece.ModulePieces;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class ModulePieceManager {
	public final List<LootReplaceModulePiece> lootReplaceModulePieces;
	public final List<BucketBreakModulePiece> bucketBreakModulePieces;

	public ModulePieceManager(UHCConfig config) {
		this.lootReplaceModulePieces = getModulesPieces(config, ModulePieces.LOOT_REPLACE);
		this.bucketBreakModulePieces = getModulesPieces(config, ModulePieces.BUCKET_BREAK);
	}

	private <V extends ModulePiece> List<V> getModulesPieces(UHCConfig config, Identifier id) {
		return config.getModulesPieces().stream().filter(item -> ModulePieces.getId(item).equals(id))
				.map(piece -> (V) piece)
				.collect(Collectors.toList());
	}
}
