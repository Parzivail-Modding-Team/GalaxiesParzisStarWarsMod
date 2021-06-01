package com.parzivail.pswg.client.texture.stacked;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.client.ColorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class StackedTexture extends ResourceTexture
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier[] textures;
	private boolean loaded;

	public StackedTexture(Identifier fallbackSkin, Collection<Identifier> textures)
	{
		super(fallbackSkin);
		this.textures = textures.toArray(new Identifier[0]);
	}

	private void onTextureLoaded(NativeImage image)
	{
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			this.loaded = true;
			if (!RenderSystem.isOnRenderThread())
			{
				RenderSystem.recordRenderCall(() -> {
					this.uploadTexture(image);
				});
			}
			else
			{
				this.uploadTexture(image);
			}
		});
	}

	private void uploadTexture(NativeImage image)
	{
		TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, true);
	}

	public void load(ResourceManager manager) throws IOException
	{
		MinecraftClient minecraft = MinecraftClient.getInstance();
		minecraft.execute(() -> {
			if (!this.loaded)
			{
				try
				{
					super.load(manager);
				}
				catch (IOException var3)
				{
					LOGGER.warn("Failed to load texture: {}", this.location, var3);
				}

				this.loaded = true;
			}
		});

		NativeImage[] nativeImages = new NativeImage[textures.length];

		for (int i = 0; i < textures.length; i++)
		{
			TextureData texData = TextureData.load(manager, textures[i]);

			nativeImages[i] = texData.getImage();

			if (nativeImages[i].getWidth() != nativeImages[0].getWidth() || nativeImages[i].getHeight() != nativeImages[0].getHeight())
				throw new IOException("All textures in a stack must be the same size");
		}

		NativeImage base = nativeImages[0];

		for (int i = 1; i < nativeImages.length; i++)
		{
			NativeImage layerImage = nativeImages[i];

			int width = layerImage.getWidth();
			int height = layerImage.getHeight();
			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
					base.setPixelColor(x, y, ColorUtil.blendColorsOnSrcAlpha(base.getPixelColor(x, y), layerImage.getPixelColor(x, y), 0xFFFFFF));
			}
		}

		this.onTextureLoaded(base);
	}
}
