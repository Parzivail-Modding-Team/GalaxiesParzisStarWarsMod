package com.parzivail.pswg.mixin;

import com.parzivail.pswg.container.SwgEntities;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin
{
	@Inject(method = "alwaysUpdateVelocity()Z", at = @At("HEAD"), cancellable = true)
	private void alwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir)
	{
		EntityType<?> self = (EntityType<?>)(Object)this;
		if (self == SwgEntities.Misc.BlasterBolt ||
		    self == SwgEntities.Misc.BlasterIonBolt)
		{
			cir.setReturnValue(false);
		}
	}
}
