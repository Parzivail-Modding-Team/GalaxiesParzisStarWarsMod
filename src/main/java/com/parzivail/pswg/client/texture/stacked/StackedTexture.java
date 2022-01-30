package com.parzivail.pswg.client.texture.stacked;

import com.parzivail.pswg.client.texture.CallbackTexture;
import com.parzivail.pswg.client.texture.remote.RemoteTexture;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.data.TintedIdentifier;
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
	private final Identifier[] textures;

	public StackedTexture(Identifier fallbackSkin, Collection<Identifier> textures, Consumer<Boolean> callback)
	{
		super(fallbackSkin, callback);
		this.textures = textures.toArray(new Identifier[0]);
	}

	@Override
	protected NativeImage generateImage(ResourceManager manager) throws IOException
	{
		var textureManager = MinecraftClient.getInstance().getTextureManager();

		var nativeImages = new NativeImage[textures.length];
		var tints = new int[textures.length];
		var tintModes = new TintedIdentifier.Mode[textures.length];

		for (var i = 0; i < textures.length; i++)
		{
			var textureId = textures[i];

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
			else if (texture instanceof RemoteTexture t)
				nativeImages[i] = t.getImage();
			else if (texture instanceof CallbackTexture t)
				nativeImages[i] = t.getImage();
			else
			{
				var texData = TextureData.load(manager, textureId);
				nativeImages[i] = texData.getImage();
			}

			if (nativeImages[i] != null && (nativeImages[i].getWidth() != nativeImages[0].getWidth() || nativeImages[i].getHeight() != nativeImages[0].getHeight()))
				throw new IOException("All textures in a stack must be the same size");
		}

		var base = nativeImages[0];

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
					base.setColor(x, y, ColorUtil.blendColorsOnSrcAlpha(base.getColor(x, y), layerImage.getColor(x, y), tints[i], tintModes[i]));
			}
		}

		return base;
	}
}
