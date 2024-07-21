package com.parzivail.util.client.texture.tinted;

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

@Environment(EnvType.CLIENT)
public class TintedTexture extends CallbackTexture
{
	private final TintedIdentifier texture;

	public TintedTexture(TintedIdentifier texture, Identifier fallbackSkin, BooleanConsumer completionCallback)
	{
		super(fallbackSkin, completionCallback);
		this.texture = texture;
	}

	@Override
	protected NativeImage generateImage(ResourceManager manager) throws IOException
	{
		var textureManager = MinecraftClient.getInstance().getTextureManager();

		var tex = textureManager.getTexture(texture);

		NativeImage nativeImage;
		if (tex instanceof NativeImageBackedTexture nibt)
			nativeImage = nibt.getImage();
		else if (tex instanceof CallbackTexture t)
			nativeImage = t.getImage();
		else
		{
			var texData = TextureData.load(manager, texture);
			nativeImage = texData.getImage();
		}

		if (nativeImage == null)
			throw new IOException("NativeImage was null");

		var width = nativeImage.getWidth();
		var height = nativeImage.getHeight();
		for (var x = 0; x < width; x++)
		{
			for (var y = 0; y < height; y++)
				nativeImage.setColor(x, y, NativeImageUtil.tint(nativeImage.getColor(x, y), texture.getTint(), texture.getTintMode()));
		}

		return nativeImage;
	}
}
