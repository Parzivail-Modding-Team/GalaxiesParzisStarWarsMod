package com.parzivail.pswg.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin
{
	@Shadow
	protected abstract Identifier getTexturePath(Identifier identifier);

	@ModifyVariable(method = "loadSprite", at = @At(value = "NEW", target = "net/minecraft/client/texture/Sprite"), ordinal = 0)
	public NativeImage spriteAddBaseLayer(NativeImage nativeImage, ResourceManager container, Sprite.Info info, int i, int j, int k, int l, int m) throws IOException
	{
		Identifier identifier = getTexturePath(info.getId());
		Resource baseLayerResource;
		if (!container.containsResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswgbaselayer")))
		{
			return nativeImage;
		}
		try
		{
			baseLayerResource = container.getResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswgbaselayer"));
		} catch (IOException ignored)
		{
			return nativeImage;
		}
		Identifier baseLayerIdentifier;
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(baseLayerResource.getInputStream(), StandardCharsets.UTF_8)))
		{
			baseLayerIdentifier = getTexturePath(new Identifier(bufferedReader.readLine()));
		}
		NativeImage baseLayerImage = NativeImage.read(container.getResource(baseLayerIdentifier).getInputStream());
		if (baseLayerImage.getHeight() != nativeImage.getHeight() || baseLayerImage.getWidth() != nativeImage.getWidth())
		{
			throw new RuntimeException("Error: Size of " + identifier + " (" + nativeImage.getWidth() + '|' + nativeImage.getHeight() + ") doesn't match its base layer's size (" + baseLayerIdentifier + ", " + baseLayerImage.getWidth() + '|' + baseLayerImage.getHeight() + ')');
		}

		int width = baseLayerImage.getWidth();
		int height = baseLayerImage.getHeight();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				baseLayerImage.blendPixel(x, y, nativeImage.getPixelRgba(x, y));
			}
		}
		return baseLayerImage;
	}
}
