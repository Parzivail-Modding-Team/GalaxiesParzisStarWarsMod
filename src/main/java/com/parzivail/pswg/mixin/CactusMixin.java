package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CactusBlock.class)
public abstract class CactusMixin
{
	@Inject(method = "canPlaceAt", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		Block below = world.getBlockState(pos.down()).getBlock();
		if (below == SwgBlocks.Sand.Tatooine || below == SwgBlocks.Sand.TatooineCanyon)
			cir.setReturnValue(true);
	}
}
