package dev.pswg.mixin.client.attributes;

import dev.pswg.Galaxies;
import dev.pswg.attributes.GalaxiesEntityAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public abstract class MouseMixin
{
	@ModifyArgs(method = "Lnet/minecraft/client/Mouse;updateMouse(D)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
	public void updateMouse$changeLookDirection(Args args)
	{
		var config = Galaxies.CONFIG.get();
		if (!config.scaleMouseWithFieldOfView)
			return;

		var client = MinecraftClient.getInstance();
		var player = client.player;
		if (player == null)
			return;

		var fovMultiplier = (float)player.getAttributeValue(GalaxiesEntityAttributes.FIELD_OF_VIEW_ZOOM);

		// cursorDeltaX
		args.set(0, (double)args.get(0) / fovMultiplier);

		// cursorDeltaY
		args.set(1, (double)args.get(1) / fovMultiplier);
	}
}
