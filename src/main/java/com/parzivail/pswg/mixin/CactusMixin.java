package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.*;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sun.util.locale.provider.LocaleProviderAdapter;

import java.util.Iterator;

@Mixin(CactusBlock.class)
public abstract class CactusMixin
{
	@Inject(method = "canPlaceAt", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void canPlaceOnTatooineSand(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		Iterator iterator = Direction.Type.HORIZONTAL.iterator();

		Direction direction;
		Material material;

		if (iterator.hasNext()) {
			BlockState blockState2 = world.getBlockState(pos.down());
			cir.setReturnValue(blockState2.isOf(Blocks.CACTUS) || blockState2.isOf(Blocks.SAND) || blockState2.isOf(Blocks.RED_SAND) || blockState2.isOf(SwgBlocks.Sand.Tatooine) || blockState2.isOf(SwgBlocks.Sand.DenseTatooine));
		}
	}

}
