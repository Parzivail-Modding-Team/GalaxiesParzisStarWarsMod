package com.parzivail.util.client.texture.stacked;

import com.parzivail.util.client.NativeImageUtil;
import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.data.TintedIdentifier;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class StackedTexture extends CallbackTexture
{
	private final Identifier transparentPlaceholder;
	private final Identifier[] textures;

	public StackedTexture(Identifier transparentTexture, Identifier fallbackSkin, Collection<Identifier> textures, BooleanConsumer callback)
	{
		super(fallbackSkin, callback);
		this.transparentPlaceholder = transparentTexture;
		this.textures = textures.toArray(new Identifier[0]);
	}

	@Override
	protected NativeImage generateImage(ResourceManager manager) throws IOException
	{
		var textureManager = MinecraftClient.getInstance().getTextureManager();

		var nativeImages = new NativeImage[textures.length];
		var tints = new int[textures.length];
		var tintModes = new TintedIdentifier.Mode[textures.length];

		NativeImage baseImage = null;

		for (var i = 0; i < textures.length; i++)
		{
			var textureId = textures[i];

			if (textureId.equals(transparentPlaceholder))
				continue;

			if (textureId instanceof TintedIdentifier ti)
			{
				tints[i] = ti.getTint();
				tintModes[i] = ti.getTintMode();
			}
			else
			{
				tints[i] = 0xFFFFFF;
				tintModes[i] = TintedIdentifier.Mode.Multiply;
			}

			var texture = textureManager.getTexture(textureId);

			if (texture instanceof NativeImageBackedTexture nibt)
				nativeImages[i] = nibt.getImage();
			else if (texture instanceof CallbackTexture t)
				nativeImages[i] = t.getImage();
			else
			{
				var texData = TextureData.load(manager, textureId);
				nativeImages[i] = texData.getImage();
			}

			if (baseImage == null)
				baseImage = nativeImages[i];

			if (nativeImages[i] != null && nativeImages[i].getWidth() == 0 && nativeImages[i].getHeight() == 0)
				throw new IOException("Failed to load texture while stacking: " + textureId);
			else if (nativeImages[i] != null && (nativeImages[i].getWidth() != baseImage.getWidth() || nativeImages[i].getHeight() != baseImage.getHeight()))
				throw new IOException("Failed to stack texture (size mismatch): " + textureId);
		}

		if (baseImage == null)
			throw new IOException("Empty texture stack");

		for (var i = 0; i < nativeImages.length; i++)
		{
			var layerImage = nativeImages[i];

			if (layerImage == null)
				continue;

			var width = layerImage.getWidth();
			var height = layerImage.getHeight();
			for (var x = 0; x < width; x++)
			{
				for (var y = 0; y < height; y++)
					baseImage.setColor(x, y, NativeImageUtil.blendColorsOnSrcAlpha(baseImage.getColor(x, y), layerImage.getColor(x, y), tints[i], tintModes[i]));
			}
		}

		return baseImage;
	}
}
