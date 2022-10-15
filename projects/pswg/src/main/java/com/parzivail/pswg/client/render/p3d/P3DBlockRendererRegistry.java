package com.parzivail.pswg.client.render.p3d;

import net.minecraft.block.Block;

import java.util.HashMap;

public class P3DBlockRendererRegistry
{
	private static final IP3DBlockRenderer defaultRenderer = (matrices, quadEmitter, target, randomSupplier, renderContext, model, baseSprite, additionalSprites) -> {
		model.renderBlock(matrices, quadEmitter, target, null, (target1, objectName) -> baseSprite, randomSupplier, renderContext);
	};

	private static final HashMap<Block, IP3DBlockRenderer> blockTransformers = new HashMap<>();

	public static void register(Block block, IP3DBlockRenderer transformer)
	{
		blockTransformers.put(block, transformer);
	}

	public static IP3DBlockRenderer get(Block block)
	{
		return blockTransformers.getOrDefault(block, defaultRenderer);
	}
}
