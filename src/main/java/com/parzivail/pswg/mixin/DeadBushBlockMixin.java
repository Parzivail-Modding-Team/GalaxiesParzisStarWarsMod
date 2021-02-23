package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeadBushBlock.class)
public class DeadBushBlockMixin
{
	@Inject(method = "canPlantOnTop", at = @At("RETURN"), cancellable = true)
	private void canPlantOnTop(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		Block block = state.getBlock();
		if (SwgTags.Block.DESERT_SAND.contains(block) || block == SwgBlocks.Dirt.DesertLoam )
			cir.setReturnValue(true);
	}
}
