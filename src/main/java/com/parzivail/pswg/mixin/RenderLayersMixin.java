package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderLayers.class)
@Environment(EnvType.CLIENT)
public interface RenderLayersMixin
{
	@Accessor("BLOCKS")
	static Map<Block, RenderLayer> getBlockRenderLayers()
	{
		throw new AssertionError();
	}

	@Accessor("FLUIDS")
	static Map<Fluid, RenderLayer> getFluidRenderLayers()
	{
		throw new AssertionError();
	}
}
