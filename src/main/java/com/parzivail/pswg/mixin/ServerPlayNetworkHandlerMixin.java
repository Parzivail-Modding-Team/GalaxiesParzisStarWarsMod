package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IFlyingVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
	@Shadow private Entity topmostRiddenEntity;

	@Shadow private int vehicleFloatingTicks;

	@Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;vehicleFloatingTicks:I"))
	private void preventFlyingVehicleKick(CallbackInfo ci)
	{
		if (topmostRiddenEntity instanceof IFlyingVehicle)
		{
			vehicleFloatingTicks = 0;
		}
	}
}
