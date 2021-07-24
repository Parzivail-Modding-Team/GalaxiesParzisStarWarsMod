package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.ship.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class EntityClientMixin
{
	@Inject(method = "changeLookDirection(DD)V", at = @At("HEAD"), cancellable = true)
	void changeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo info)
	{
		if (ShipEntity.handleMouseInput(cursorDeltaX, cursorDeltaY))
			info.cancel();
	}
}
