package com.parzivail.pswg.mixin;

import com.parzivail.util.client.model.DynamicBakedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
@Environment(EnvType.CLIENT)
public abstract class AbstractBlockStateMixin
{
	@Inject(method = "getModelOffset", at = @At("HEAD"))
	private void getModelOffset(BlockView world, BlockPos pos, CallbackInfoReturnable<Vec3d> cir)
	{
		DynamicBakedModel.cacheBlockPosQuery(world, pos);
	}
}
