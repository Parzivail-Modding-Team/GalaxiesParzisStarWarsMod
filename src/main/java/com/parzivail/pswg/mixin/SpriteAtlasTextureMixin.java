package com.parzivail.pswg.mixin;

import com.google.gson.Gson;
import com.parzivail.pswg.json.PSWGTextureMeta;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin
{
	@Shadow
	protected abstract Identifier getTexturePath(Identifier identifier);

	@Unique
	private static final Gson GSON = new Gson();

	@ModifyVariable(method = "loadSprite", at = @At(value = "NEW", target = "net/minecraft/client/texture/Sprite"), ordinal = 0)
	public NativeImage spriteAddBaseLayer(NativeImage nativeImage, ResourceManager container, Sprite.Info info, int i, int j, int k, int l, int m) throws IOException
	{
		Identifier identifier = getTexturePath(info.getId());
		Resource textureMetaResource;
		if (!container.containsResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswgmeta")))
		{
			return nativeImage;
		}
		try
		{
			textureMetaResource = container.getResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswgmeta"));
		} catch (IOException ignored)
		{
			return nativeImage;
		}
		PSWGTextureMeta textureMeta;
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textureMetaResource.getInputStream(), StandardCharsets.UTF_8)))
		{
			textureMeta = GSON.fromJson(bufferedReader, PSWGTextureMeta.class);
		}
		if (textureMeta.layers == null)
		{
			return nativeImage;
		}

		NativeImage[] layerImages = new NativeImage[textureMeta.layers.size()];
		int[] layerTints = new int[textureMeta.layers.size()];
		List<PSWGTextureMeta.Layer> layers = textureMeta.layers;
		{
			int n = 0;
			for (PSWGTextureMeta.Layer layer : layers)
			{
				if (layer.layer.equals("#this"))
				{
					layerImages[n] = nativeImage;
				} else
				{
					// what should happen if a base layer has a .pswgmeta file too?
					Identifier baseLayerIdentifier = getTexturePath(new Identifier(layer.layer));
					NativeImage baseLayerImage = NativeImage.read(container.getResource(baseLayerIdentifier).getInputStream());
					if (baseLayerImage.getHeight() != nativeImage.getHeight() || baseLayerImage.getWidth() != nativeImage.getWidth())
					{
						throw new RuntimeException("Error: Size of " + identifier + " (" + nativeImage.getWidth() + '|' + nativeImage.getHeight() + ") doesn't match its base layer's size (" + baseLayerIdentifier + ", " + baseLayerImage.getWidth() + '|' + baseLayerImage.getHeight() + ')');
					}
					layerImages[n] = baseLayerImage;
				}
				layerTints[n++] = layer.tint;
			}
		}

		NativeImage outImage = new NativeImage(nativeImage.getWidth(), nativeImage.getHeight(), false);

		int width = nativeImage.getWidth();
		int height = nativeImage.getHeight();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				for (int n = 0, layerImagesLength = layerImages.length; n < layerImagesLength; n++)
				{
					NativeImage layerImage = layerImages[n];
					outImage.blendPixel(x, y, applyTint(layerImage.getPixelRgba(x, y), layerTints[n]));
				}
			}
		}
		return outImage;
	}

	@Unique
	private int applyTint(int pixel, int tint)
	{
		return ((int) ((pixel & 255) * ((tint & 255) / 255f))) | (int) ((pixel >> 8 & 255) * ((tint >> 8 & 255) / 255f)) << 8 | (int) ((pixel >> 16 & 255) * ((tint >> 16 & 255) / 255f)) << 16 | (pixel & 0xff000000);
	}
}
