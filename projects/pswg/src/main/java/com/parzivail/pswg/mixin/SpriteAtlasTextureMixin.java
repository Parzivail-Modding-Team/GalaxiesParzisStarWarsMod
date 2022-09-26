package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.util.client.sprite.LayeredSpriteBuilder;
import com.parzivail.util.client.texture.CallbackTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(SpriteAtlasTexture.class)
@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasTextureMixin
{
	@Shadow
	protected abstract Identifier getTexturePath(Identifier identifier);

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "loadSprite", at = @At("HEAD"), cancellable = true)
	public void spriteAddBaseLayer(ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y, CallbackInfoReturnable<Sprite> cir) throws IOException
	{
		var textureManager = MinecraftClient.getInstance().getTextureManager();

		var id = info.getId();
		if (id.getNamespace().equals(Resources.MODID) && id.getPath().startsWith("///"))
		{
			Galaxies.LOG.debug("Trying to inject sprite %s", id);
			var texture = textureManager.getTexture(id);
			if (texture instanceof CallbackTexture ct)
			{
				var image = ct.getImage();
				Galaxies.LOG.debug("Success (%s,%s)", image.getWidth(), image.getHeight());
				cir.setReturnValue(new Sprite((SpriteAtlasTexture)(Object)this, info, maxLevel, atlasWidth, atlasHeight, x, y, image));
			}
			else
				Galaxies.LOG.debug("Failed, was %s (not CallbackTexture)", texture == null ? "[null]" : texture.getClass().getSimpleName());
		}
	}

	@ModifyVariable(method = "loadSprite", at = @At(value = "NEW", target = "net/minecraft/client/texture/Sprite"), ordinal = 0)
	public NativeImage spriteAddBaseLayer(NativeImage nativeImage, ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) throws IOException
	{
		return LayeredSpriteBuilder.build(nativeImage, getTexturePath(info.getId()), container, this::getTexturePath);
	}

	@Inject(method = "loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	public void loadSprites(ResourceManager resourceManager, Set<Identifier> ids, CallbackInfoReturnable<Collection<Sprite.Info>> cir, List<CompletableFuture<?>> list, Queue<Sprite.Info> spriteInfo) throws IOException
	{
		var textureManager = MinecraftClient.getInstance().getTextureManager();

		for (var id : List.copyOf(ids))
		{
			if (id.getNamespace().equals(Resources.MODID) && id.getPath().startsWith("///"))
			{
				Galaxies.LOG.debug("Trying to load sprite %s", id);
				var texture = textureManager.getTexture(id);
				if (texture instanceof CallbackTexture ct)
				{
					var image = ct.getImage();
					spriteInfo.add(new Sprite.Info(id, image.getWidth(), image.getHeight(), AnimationResourceMetadata.EMPTY));

					ids.remove(id);

					Galaxies.LOG.debug("Success (%s,%s)", image.getWidth(), image.getHeight());
				}
				else
					Galaxies.LOG.debug("Failed, was %s (not CallbackTexture)", texture == null ? "[null]" : texture.getClass().getSimpleName());
			}
		}
	}
}
