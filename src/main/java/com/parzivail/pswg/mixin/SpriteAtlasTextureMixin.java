package com.parzivail.pswg.mixin;

import com.google.gson.Gson;
import com.parzivail.pswg.json.PSWGTextureMeta;
import com.parzivail.pswg.util.Lumberjack;
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
		catch (IOException ex)
		{
			Lumberjack.error("Error loading pswgmeta resource:");
			ex.printStackTrace();
			return nativeImage;
		}

		PSWGTextureMeta textureMeta;
		try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textureMetaResource.getInputStream(), StandardCharsets.UTF_8)))
		{
			textureMeta = GSON.fromJson(bufferedReader, PSWGTextureMeta.class);
		}
		catch (Exception ex)
		{
			Lumberjack.error("Error loading pswgmeta JSON:");
			ex.printStackTrace();
			return nativeImage;
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
					throw new RuntimeException("Layer size mismatch: overlay layer " + identifier + " (" + nativeImage.getWidth() + "x" + nativeImage.getHeight() + "), base layer " + baseLayerIdentifier + " (" + baseLayerImage.getWidth() + "x" + baseLayerImage.getHeight() + ")");

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
		int pixelA = ColorUtil.Argb.getA(color);
		int pixelR = ColorUtil.Argb.getR(color);
		int pixelG = ColorUtil.Argb.getG(color);
		int pixelB = ColorUtil.Argb.getB(color);

		int tintA = ColorUtil.Argb.getA(tint);
		int tintR = ColorUtil.Argb.getR(tint);
		int tintG = ColorUtil.Argb.getG(tint);
		int tintB = ColorUtil.Argb.getB(tint);

		int a = pixelA * tintA / 0xFF;
		int r = pixelR * tintR / 0xFF;
		int b = pixelB * tintB / 0xFF;
		int g = pixelG * tintG / 0xFF;

		return ColorUtil.Argb.pack(a, r, g, b);
	}
}
