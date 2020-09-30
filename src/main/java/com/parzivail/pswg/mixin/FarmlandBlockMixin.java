package com.parzivail.pswg.mixin;

import com.parzivail.pswg.block.IMoistureProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin
{
	@Inject(method = "isWaterNearby(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
	private static void isWaterNearby(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
	{
		Iterator<BlockPos> var2 = BlockPos.iterate(pos.add(-4, 1, -4), pos.add(4, 1, 4)).iterator();

		for (BlockPos searchPos = var2.next(); var2.hasNext(); searchPos = var2.next())
		{
			BlockState state = world.getBlockState(searchPos);
			if (state.getBlock() instanceof IMoistureProvider)
			{
				if (((IMoistureProvider)state.getBlock()).providesMoisture(world, pos, state))
					cir.setReturnValue(true);
			}
		}
	}
}
