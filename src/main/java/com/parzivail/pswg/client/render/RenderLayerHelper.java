package com.parzivail.pswg.client.render;

import com.parzivail.pswg.mixin.RenderLayersMixin;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

public class RenderLayerHelper
{
	public static void addBlock(Block block, RenderLayer layer)
	{
		RenderLayersMixin.getBlockRenderLayers().put(block, layer);
	}
}
