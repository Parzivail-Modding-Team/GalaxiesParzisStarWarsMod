package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.texture.remote.RemoteTextureProvider;
import com.parzivail.pswg.client.texture.stacked.StackedTextureProvider;
import com.parzivail.pswg.handler.LeftClickHandler;
import com.parzivail.util.Lumberjack;
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

import java.nio.file.Path;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin
{
	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	@Nullable
	public ClientPlayerInteractionManager interactionManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(RunArgs args, CallbackInfo ci)
	{
		Path remoteAssetDir = args.directories.assetDir.toPath().resolve("pswgRemoteAssets");
		Lumberjack.debug("Remote asset directory: %s", remoteAssetDir.toString());
		Client.remoteTextureProvider = new RemoteTextureProvider(textureManager, "pswg:remote", remoteAssetDir);
		Client.stackedTextureProvider = new StackedTextureProvider(textureManager, "pswg:stacked");
	}

	@Inject(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V", shift = At.Shift.BEFORE), cancellable = true)
	private void handleInputEventsHandleBlockBreaking(CallbackInfo ci)
	{
		// interactionManager cannot be null here
		assert interactionManager != null;
		LeftClickHandler.handleInputEvents(ci, interactionManager);
	}

	@Inject(method = "doAttack()V", at = @At("HEAD"), cancellable = true)
	private void doAttack(CallbackInfo ci)
	{
		LeftClickHandler.doAttack(ci);
	}
}
