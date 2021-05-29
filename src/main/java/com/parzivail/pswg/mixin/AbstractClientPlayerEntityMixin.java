package com.parzivail.pswg.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class AbstractClientPlayerEntityMixin
{
//	@Inject(method = "getSkinTexture", at = @At(value = "HEAD"), cancellable = true)
//	private void getSkinTexture(CallbackInfoReturnable<Identifier> cir)
//	{
//		SwgPersistentComponents pc = SwgEntityComponents.getPersistent((PlayerEntity)(Object)this);
//
//		SwgSpecies species = pc.getSpecies();
//		if (species == null)
//			return;
//
//		cir.setReturnValue(SwgSpeciesModels.getTexture(species));
//		cir.cancel();
//	}
}
