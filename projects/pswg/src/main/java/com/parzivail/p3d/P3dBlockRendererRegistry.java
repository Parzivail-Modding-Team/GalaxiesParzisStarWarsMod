package com.parzivail.p3d;

import net.minecraft.block.Block;

import java.util.HashMap;

public class P3dBlockRendererRegistry
{
	private static final IP3dBlockRenderer defaultRenderer = (matrices, quadEmitter, target, randomSupplier, renderContext, model, baseSprite, additionalSprites) -> {
		model.renderBlock(matrices, quadEmitter, target, null, (target1, objectName) -> baseSprite, randomSupplier, renderContext);
	};

	private static final HashMap<Block, IP3dBlockRenderer> blockTransformers = new HashMap<>();

	public static void register(Block block, IP3dBlockRenderer transformer)
	{
		blockTransformers.put(block, transformer);
	}

	public static IP3dBlockRenderer get(Block block)
	{
		return blockTransformers.getOrDefault(block, defaultRenderer);
	}
}
