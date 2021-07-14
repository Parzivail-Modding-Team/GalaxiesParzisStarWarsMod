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
import net.minecraft.resource.ReloadableResourceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin
{
	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Shadow
	@Nullable
	public ClientPlayerInteractionManager interactionManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(RunArgs args, CallbackInfo ci)
	{
		var remoteAssetDir = args.directories.assetDir.toPath().resolve("pswgRemoteAssets");
		Lumberjack.debug("Remote asset directory: %s", remoteAssetDir.toString());
		Client.remoteTextureProvider = new RemoteTextureProvider(textureManager, "pswg:remote", remoteAssetDir);
		Client.stackedTextureProvider = new StackedTextureProvider(textureManager, "pswg:stacked");
	}

	@Inject(method = "Lnet/minecraft/client/MinecraftClient;initializeSearchableContainers()V", at = @At("TAIL"))
	private void initializeSearchableContainers(CallbackInfo ci)
	{
		// Registering the reloadable resource managers here because this method is
		// only called once and it's before the tail of MinecraftClient's <init>,
		// where the resource manager is reloaded

		Client.ResourceManagers.registerReloadableManagers(resourceManager);
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
