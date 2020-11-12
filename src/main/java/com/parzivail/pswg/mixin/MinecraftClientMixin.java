package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.remote.RemoteTextureProvider;
import com.parzivail.util.item.LeftClickHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.texture.TextureManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin
{
	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(RunArgs args, CallbackInfo ci)
	{
		Client.remoteTextureProvider = new RemoteTextureProvider(textureManager, "pswg:remote", new File(args.directories.assetDir, "pswgRemoteAssets"));
	}

	@Inject(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V", shift = At.Shift.BEFORE), cancellable = true)
	private void handleInputEvents(CallbackInfo ci)
	{
		// interactionManager cannot be null here
		LeftClickHandler.handleInputEvents(ci, interactionManager);
	}
}
