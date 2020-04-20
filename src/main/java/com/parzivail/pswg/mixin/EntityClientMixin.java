package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.input.ShipInputHandler;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityClientMixin
{
	@Inject(method = "changeLookDirection(DD)V", at = @At("HEAD"), cancellable = true)
	void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo info)
	{
		if (ShipInputHandler.handle(cursorDeltaX, cursorDeltaY))
			info.cancel();
	}
}
