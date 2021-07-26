package com.parzivail.pswg.mixin;

import com.parzivail.util.block.BushLeavesBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RenderLayers.class)
@Environment(EnvType.CLIENT)
public class RenderLayersMixin
{
	@Shadow
	@Final
	private static Map<Block, RenderLayer> BLOCKS;

	@Inject(method = "Lnet/minecraft/client/render/RenderLayers;getBlockLayer(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/RenderLayer;", at = @At("HEAD"), cancellable = true)
	private static void getBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir)
	{
		if (state.getBlock() instanceof BushLeavesBlock)
		{
			var renderLayer = BLOCKS.get(state.getBlock());
			cir.setReturnValue(renderLayer != null ? renderLayer : RenderLayer.getSolid());
		}
	}
}
