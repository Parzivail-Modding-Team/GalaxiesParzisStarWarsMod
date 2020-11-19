package com.parzivail.pswg.client.texture.remote;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class RemoteTexture extends ResourceTexture
{
	private static final Logger LOGGER = LogManager.getLogger();
	@Nullable
	private final File cacheFile;
	private final String url;
	@Nullable
	private CompletableFuture<?> loader;
	private boolean loaded;

	public RemoteTexture(@Nullable File cacheFile, String url, Identifier fallbackSkin)
	{
		super(fallbackSkin);
		this.cacheFile = cacheFile;
		this.url = url;
	}

	private void onTextureLoaded(NativeImage image)
	{
		Client.minecraft.execute(() -> {
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
		TextureUtil.allocate(this.getGlId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, true);
	}

	public void load(ResourceManager manager) throws IOException
	{
		Client.minecraft.execute(() -> {
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
		if (this.loader == null)
		{
			NativeImage nativeImage2;
			if (this.cacheFile != null && this.cacheFile.isFile())
			{
				LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
				FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
				nativeImage2 = this.loadTexture(fileInputStream);
			}
			else
			{
				nativeImage2 = null;
			}

			if (nativeImage2 != null)
			{
				this.onTextureLoaded(nativeImage2);
			}
			else
			{
				this.loader = CompletableFuture.runAsync(() -> {
					HttpURLConnection httpURLConnection = null;
					LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);

					try
					{
						httpURLConnection = (HttpURLConnection)(new URL(this.url)).openConnection(Client.minecraft.getNetworkProxy());
						httpURLConnection.setDoInput(true);
						httpURLConnection.setDoOutput(false);
						httpURLConnection.connect();
						if (httpURLConnection.getResponseCode() / 100 == 2)
						{
							InputStream inputStream2;
							if (this.cacheFile != null)
							{
								FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), this.cacheFile);
								inputStream2 = new FileInputStream(this.cacheFile);
							}
							else
							{
								inputStream2 = httpURLConnection.getInputStream();
							}

							Client.minecraft.execute(() -> {
								NativeImage nativeImage = this.loadTexture(inputStream2);
								if (nativeImage != null)
								{
									this.onTextureLoaded(nativeImage);
								}
							});
							return;
						}
					}
					catch (Exception var6)
					{
						LOGGER.error("Couldn't download http texture", var6);
						return;
					}
					finally
					{
						if (httpURLConnection != null)
						{
							httpURLConnection.disconnect();
						}
					}
				}, Util.getMainWorkerExecutor());
			}
		}
	}

	@Nullable
	private NativeImage loadTexture(InputStream stream)
	{
		NativeImage nativeImage = null;

		try
		{
			nativeImage = NativeImage.read(stream);
		}
		catch (IOException var4)
		{
			LOGGER.warn("Error while loading the remote texture", var4);
		}

		return nativeImage;
	}
}
