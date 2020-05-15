package com.parzivail.pswg.mixin;

import com.google.gson.Gson;
import com.parzivail.pswg.json.PSWGTextureMeta;
import com.parzivail.util.ColorUtil;
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
			return nativeImage;

		try
		{
			textureMetaResource = container.getResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswgmeta"));
		}
		catch (IOException ignored)
		{
			return nativeImage;
		}

		PSWGTextureMeta textureMeta;
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textureMetaResource.getInputStream(), StandardCharsets.UTF_8)))
		{
			textureMeta = GSON.fromJson(bufferedReader, PSWGTextureMeta.class);
		}

		if (textureMeta.layers == null)
			return nativeImage;

		NativeImage[] layerImages = new NativeImage[textureMeta.layers.length];
		int[] layerTints = new int[textureMeta.layers.length];

		for (int n = 0; n < textureMeta.layers.length; n++)
		{
			PSWGTextureMeta.Layer layer = textureMeta.layers[n];

			if (layer.texture.equals("#this"))
				layerImages[n] = nativeImage;
			else
			{
				// what should happen if a base layer has a .pswgmeta file too?
				Identifier baseLayerIdentifier = getTexturePath(new Identifier(layer.texture));
				NativeImage baseLayerImage = NativeImage.read(container.getResource(baseLayerIdentifier).getInputStream());

				if (baseLayerImage.getHeight() != nativeImage.getHeight() || baseLayerImage.getWidth() != nativeImage.getWidth())
					throw new RuntimeException("Error: Size of " + identifier + " (" + nativeImage.getWidth() + '|' + nativeImage.getHeight() + ") doesn't match its base layer's size (" + baseLayerIdentifier + ", " + baseLayerImage.getWidth() + '|' + baseLayerImage.getHeight() + ')');

				layerImages[n] = baseLayerImage;
			}

			layerTints[n] = layer.tint;
		}

		NativeImage outImage = new NativeImage(nativeImage.getWidth(), nativeImage.getHeight(), false);

		int width = nativeImage.getWidth();
		int height = nativeImage.getHeight();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				for (int layer = 0, layerImagesLength = layerImages.length; layer < layerImagesLength; layer++)
				{
					NativeImage layerImage = layerImages[layer];
					outImage.blendPixel(x, y, applyTint(layerImage.getPixelRgba(x, y), layerTints[layer]));
				}
			}
		}
		return outImage;
	}

	@Unique
	private int applyTint(int color, int tint)
	{
		int pixelA = ColorUtil.Abgr.getA(color);
		int pixelB = ColorUtil.Abgr.getB(color);
		int pixelG = ColorUtil.Abgr.getG(color);
		int pixelR = ColorUtil.Abgr.getR(color);

		int tintA = ColorUtil.Abgr.getA(tint);
		int tintB = ColorUtil.Abgr.getB(tint);
		int tintG = ColorUtil.Abgr.getG(tint);
		int tintR = ColorUtil.Abgr.getR(tint);

		int a = pixelA * tintA / 0xFF;
		int b = pixelB * tintB / 0xFF;
		int g = pixelG * tintG / 0xFF;
		int r = pixelR * tintR / 0xFF;

		return ColorUtil.Abgr.pack(a, b, g, r);
	}
}
