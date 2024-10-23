package dev.pswg.mixin.client;

import dev.pswg.interaction.GalaxiesEntityLeftClickClientManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Inject(method = "handleInputEvents()V", at = @At("HEAD"))
	public void handleInputEvents(CallbackInfo ci)
	{
		GalaxiesEntityLeftClickClientManager.handleInputEvents((MinecraftClient)(Object)this);
	}
}
