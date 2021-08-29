package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.weapon.RecoilManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public abstract class CameraMixin
{
	@Shadow
	private float pitch;
	@Shadow
	private float yaw;

	@Shadow
	protected abstract void setRotation(float yaw, float pitch);

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

	@Inject(method = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER))
	private void updateSetRotation(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		var minecraft = MinecraftClient.getInstance();

		if (focusedEntity == minecraft.player)
			this.setRotation(this.yaw + RecoilManager.getHorizontalRecoilMovement(tickDelta), this.pitch + RecoilManager.getVerticalRecoilMovement(tickDelta));
	}
}
