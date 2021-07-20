package com.parzivail.pswg.mixin;

import com.parzivail.util.item.IZoomingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Author: EnnuiL
 * Source: https://github.com/EnnuiL/LibZoomer/blob/main/src/main/java/io/github/ennuil/libzoomer/mixin/MouseMixin.java
 * License: MIT
 * TODO: officially move to LibZoomer when they're on Maven
 */
@Mixin(Mouse.class)
@Environment(EnvType.CLIENT)
public class MouseMixin
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Unique
	private boolean modifyMouse;

	@Unique
	private double finalCursorDeltaX;

	@Unique
	private double finalCursorDeltaY;

	@Inject(
			at = @At(
					value = "FIELD",
					target = "net/minecraft/client/Mouse.cursorDeltaX:D",
					ordinal = 4
			),
			method = "updateMouse()V",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void applyZoomChanges(CallbackInfo ci, double e, double o, double p)
	{
		var mainHandStack = this.client.player.getInventory().getMainHandStack();

		this.modifyMouse = false;

		if (mainHandStack.getItem() instanceof IZoomingItem)
		{
			var mult = ((IZoomingItem)mainHandStack.getItem()).getFovMultiplier(mainHandStack, client.world, this.client.player);
			o *= mult;
			p *= mult;
			this.modifyMouse = true;
		}

		this.finalCursorDeltaX = o;
		this.finalCursorDeltaY = p;
	}

	@ModifyArgs(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/tutorial/TutorialManager.onUpdateMouse(DD)V"
			),
			method = "updateMouse()V"
	)
	private void modifyTutorialUpdate(Args args)
	{
		if (!this.modifyMouse)
			return;
		args.set(0, finalCursorDeltaX);
		args.set(1, finalCursorDeltaY);
	}

	@ModifyArgs(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/network/ClientPlayerEntity.changeLookDirection(DD)V"
			),
			method = "updateMouse()V"
	)
	private void modifyLookDirection(Args args)
	{
		if (!this.modifyMouse)
			return;
		args.set(0, finalCursorDeltaX);
		args.set(1, finalCursorDeltaY * (this.client.options.invertYMouse ? -1 : 1));
	}
}
