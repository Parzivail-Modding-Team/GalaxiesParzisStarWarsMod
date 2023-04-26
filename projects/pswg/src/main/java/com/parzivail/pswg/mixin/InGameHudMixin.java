package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.camera.MutableCameraEntity;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.client.render.ICustomHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"), cancellable = true)
	public void renderCrosshair(MatrixStack matrices, CallbackInfo ci)
	{
		assert this.client.player != null;

		var mainHandStack = this.client.player.getMainHandStack();
		var customHUDRenderer = ICustomHudRenderer.REGISTRY.get(mainHandStack.getItem().getClass());
		if (customHUDRenderer != null && customHUDRenderer.renderCrosshair(this.client.player, Hand.MAIN_HAND, mainHandStack, matrices))
			ci.cancel();

		var offHandStack = this.client.player.getOffHandStack();
		customHUDRenderer = ICustomHudRenderer.REGISTRY.get(offHandStack.getItem().getClass());
		if (customHUDRenderer != null && customHUDRenderer.renderCrosshair(this.client.player, Hand.OFF_HAND, offHandStack, matrices))
			ci.cancel();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F", shift = At.Shift.AFTER))
	public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci)
	{
		if (!this.client.options.getPerspective().isFirstPerson())
			return;

		assert this.client.player != null;

		var mainHandStack = this.client.player.getInventory().getMainHandStack();
		var customHUDRenderer = ICustomHudRenderer.REGISTRY.get(mainHandStack.getItem().getClass());
		if (customHUDRenderer != null)
		{
			customHUDRenderer.renderOverlay(this.client.player, Hand.MAIN_HAND, mainHandStack, matrices, scaledWidth, scaledHeight, tickDelta);
		}
	}

	@Inject(method = "getCameraPlayer()Lnet/minecraft/entity/player/PlayerEntity;", at = @At("HEAD"), cancellable = true)
	void getCameraPlayer(CallbackInfoReturnable<PlayerEntity> cir)
	{
		var camEntity = this.client.getCameraEntity();
		if (camEntity instanceof ShipEntity || camEntity instanceof MutableCameraEntity)
			cir.setReturnValue(this.client.player);
	}
}
