package com.parzivail.pswg.client.texture.tinted.stacked;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.client.texture.remote.RemoteTexture;
import com.parzivail.pswg.client.texture.stacked.StackedTexture;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.data.TintedIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class TintedTexture extends ResourceTexture
{
	private final Identifier identifier;
	private final TintedIdentifier textureId;
	private boolean loaded;

	private NativeImage image;

	public TintedTexture(Identifier identifier, Identifier fallbackSkin, TintedIdentifier texture)
	{
		super(fallbackSkin);
		this.identifier = identifier;
		this.textureId = texture;
	}

	public NativeImage getImage()
	{
		return image;
	}

	private void onTextureLoaded(NativeImage image)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			this.loaded = true;
			if (!RenderSystem.isOnRenderThread())
			{
				RenderSystem.recordRenderCall(() -> this.onTextureLoaded(image));
			}
			else
			{
				this.uploadTexture(image);
				this.image = image;
			}
		});
	}

	private void uploadTexture(NativeImage image)
	{
		TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, true);
	}

	private void onRemoteTextureResolved(RemoteTexture remoteTexture)
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.getTextureManager().destroyTexture(identifier);
	}

	public void load(ResourceManager manager) throws IOException
	{
		var minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			if (!this.loaded)
			{
				try
				{
					super.load(manager);
				}
				catch (IOException var3)
				{
					Lumberjack.warn("Failed to load texture: %s", this.location);
					var3.printStackTrace();
				}

				this.loaded = true;
			}
		});

		var textureManager = minecraft.getTextureManager();

		var tex = textureManager.getTexture(textureId);

		NativeImage nativeImage;
		if (tex instanceof NativeImageBackedTexture nibt)
			nativeImage = nibt.getImage();
		else if (tex instanceof RemoteTexture rt)
			nativeImage = rt.getImage();
		else if (tex instanceof StackedTexture st)
			nativeImage = st.getImage();
		else
		{
			var texData = TextureData.load(manager, textureId);
			nativeImage = texData.getImage();
		}

		if (nativeImage == null)
			throw new IOException("NativeImage was null");

		var width = nativeImage.getWidth();
		var height = nativeImage.getHeight();
		for (var x = 0; x < width; x++)
		{
			for (var y = 0; y < height; y++)
				nativeImage.setColor(x, y, ColorUtil.tint(nativeImage.getColor(x, y), textureId.getTint(), textureId.getTintMode()));
		}

		this.onTextureLoaded(nativeImage);
	}
}
