package com.parzivail.pswg.mixin;

import com.parzivail.pswg.entity.ship.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public class CameraMixin
{
	@ModifyArg(
			method = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(D)D"),
			index = 0
	)
	private double clipToSpace(double desiredCameraDistance)
	{
		var minecraft = MinecraftClient.getInstance();
		assert minecraft.player != null;

		var ship = ShipEntity.getShip(minecraft.player);
		if (ship != null && !ship.usePlayerPerspective())
			return 0;

		return desiredCameraDistance;
	}
}
