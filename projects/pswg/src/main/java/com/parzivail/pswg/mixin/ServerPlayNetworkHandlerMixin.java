package com.parzivail.pswg.mixin;

import com.parzivail.util.entity.IFlyingVehicle;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
	@Shadow
	public ServerPlayerEntity player;

	@Shadow
	private Entity topmostRiddenEntity;

	@Shadow
	private int vehicleFloatingTicks;

	@Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;vehicleFloatingTicks:I"))
	private void tick(CallbackInfo ci)
	{
		// Prevent flying vehicle kick
		if (topmostRiddenEntity instanceof IFlyingVehicle)
			vehicleFloatingTicks = 0;
	}
}
