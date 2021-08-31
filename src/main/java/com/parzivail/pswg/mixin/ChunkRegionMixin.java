package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgDimensions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.ChunkRegion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRegion.class)
public class ChunkRegionMixin
{
	@Shadow
	@Final
	private ServerWorld world;

	@Inject(method = "getSeed()J", at = @At("HEAD"), cancellable = true)
	private void getSeed(CallbackInfoReturnable<Long> cir)
	{
		Identifier worldId = world.getRegistryKey().getValue();
		if (SwgDimensions.FIXED_SEED_REGISTRY.containsKey(worldId))
			cir.setReturnValue(SwgDimensions.FIXED_SEED_REGISTRY.get(worldId));
	}
}
