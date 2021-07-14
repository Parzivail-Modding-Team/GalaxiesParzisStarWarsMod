package com.parzivail.pswg.client.sprite;

import com.google.gson.Gson;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.client.ColorUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class LayeredSpriteBuilder
{
	private static final Gson GSON = new Gson();

	public static NativeImage build(NativeImage nativeImage, Identifier identifier, ResourceManager resourceManager, Function<Identifier, Identifier> texturePathResolver) throws IOException
	{
		Resource textureMetaResource;
		try
		{
			textureMetaResource = resourceManager.getResource(new Identifier(identifier.getNamespace(), identifier.getPath() + ".pswglayers"));
		}
		catch (IOException ex)
		{
			Lumberjack.error("Error loading pswglayers resource:");
			ex.printStackTrace();
			return nativeImage;
		}

		SwgSpriteMetaLayered textureMeta;
		try (final var bufferedReader = new BufferedReader(new InputStreamReader(textureMetaResource.getInputStream(), StandardCharsets.UTF_8)))
		{
			textureMeta = GSON.fromJson(bufferedReader, SwgSpriteMetaLayered.class);
		}
		catch (Exception ex)
		{
			Lumberjack.error("Error loading pswglayers JSON:");
			ex.printStackTrace();
			return nativeImage;
		}

		if (textureMeta.layers == null)
			return nativeImage;

		var layerImages = new NativeImage[textureMeta.layers.length];
		var layerTints = new int[textureMeta.layers.length];

		for (var n = 0; n < textureMeta.layers.length; n++)
		{
			var layer = textureMeta.layers[n];

			if (layer.texture.equals("#this"))
				layerImages[n] = nativeImage;
			else
			{
				// what should happen if a base layer has a .pswglayers file too?
				var baseLayerIdentifier = texturePathResolver.apply(new Identifier(layer.texture));
				var baseLayerImage = NativeImage.read(resourceManager.getResource(baseLayerIdentifier).getInputStream());

				if (baseLayerImage.getHeight() != nativeImage.getHeight() || baseLayerImage.getWidth() != nativeImage.getWidth())
					throw new RuntimeException("Layer size mismatch: overlay layer " + identifier + " (" + nativeImage.getWidth() + "x" + nativeImage.getHeight() + "), base layer " + baseLayerIdentifier + " (" + baseLayerImage.getWidth() + "x" + baseLayerImage.getHeight() + ")");

				layerImages[n] = baseLayerImage;
			}

			layerTints[n] = layer.tint;
		}

		var outImage = new NativeImage(nativeImage.getWidth(), nativeImage.getHeight(), false);

		var width = nativeImage.getWidth();
		var height = nativeImage.getHeight();
		for (var x = 0; x < width; x++)
		{
			for (var y = 0; y < height; y++)
			{
				for (int layer = 0, layerImagesLength = layerImages.length; layer < layerImagesLength; layer++)
				{
					var layerImage = layerImages[layer];
					outImage.setPixelColor(x, y, ColorUtil.blendColorsOnSrcAlpha(outImage.getPixelColor(x, y), layerImage.getPixelColor(x, y), layerTints[layer]));
				}
			}
		}

		return outImage;
	}
}
