package com.parzivail.aurek.mixin;

import com.parzivail.aurek.imgui.ImGuiHelper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	@Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("TAIL"))
	private void init(CallbackInfo ci)
	{
		ImGuiHelper.init();
	}

	@Inject(method = "render(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.BEFORE))
	private void preRender(CallbackInfo ci)
	{
		ImGuiHelper.startFrame();
	}

	@Inject(method = "render(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
	private void postRender(CallbackInfo ci)
	{
		ImGuiHelper.endFrame();
	}

	@Inject(method = "render(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;swapBuffers()V", shift = At.Shift.BEFORE))
	private void swapBuffers(CallbackInfo ci)
	{
		ImGuiHelper.swapBuffers();
	}
}
