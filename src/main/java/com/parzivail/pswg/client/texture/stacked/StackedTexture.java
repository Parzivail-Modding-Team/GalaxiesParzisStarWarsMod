package com.parzivail.pswg.client.texture.stacked;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.client.texture.remote.RemoteTexture;
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
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class StackedTexture extends ResourceTexture
{
	private final Identifier identifier;
	private final Identifier[] textures;
	private boolean loaded;

	private NativeImage image;

	public StackedTexture(Identifier identifier, Identifier fallbackSkin, Collection<Identifier> textures)
	{
		super(fallbackSkin);
		this.identifier = identifier;
		this.textures = textures.toArray(new Identifier[0]);
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
			else if (texture instanceof RemoteTexture rt)
				nativeImages[i] = rt.getImage();
			else if (texture instanceof StackedTexture st)
				nativeImages[i] = st.getImage();
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
					base.setPixelColor(x, y, ColorUtil.blendColorsOnSrcAlpha(base.getPixelColor(x, y), layerImage.getPixelColor(x, y), tints[i], tintModes[i]));
			}
		}

		this.onTextureLoaded(base);
	}
}
