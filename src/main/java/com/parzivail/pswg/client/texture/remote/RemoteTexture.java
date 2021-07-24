package com.parzivail.pswg.client.texture.remote;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.Lumberjack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class RemoteTexture extends ResourceTexture
{
	@Nullable
	private final Path cacheFile;
	private final String url;
	private final Runnable onResolved;
	@Nullable
	private CompletableFuture<?> loader;
	private boolean loaded;

	private NativeImage image;

	public RemoteTexture(@Nullable Path cacheFile, String url, Identifier fallbackSkin, Runnable onResolved)
	{
		super(fallbackSkin);
		this.cacheFile = cacheFile;
		this.url = url;
		this.onResolved = onResolved;
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
				onResolved.run();
			}
		});
	}

	private void uploadTexture(NativeImage image)
	{
		TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, false);
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

		if (this.loader == null)
		{
			NativeImage nativeImage2;
			if (this.cacheFile != null && Files.isRegularFile(cacheFile))
			{
				Lumberjack.debug("Loading http texture from local cache (%s)", this.cacheFile);
				var inputStream = Files.newInputStream(this.cacheFile);
				nativeImage2 = this.loadTexture(inputStream);
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
					Lumberjack.debug("Downloading http texture from %s to %s", this.url, this.cacheFile);

					try
					{
						httpURLConnection = (HttpURLConnection)new URL(this.url).openConnection(minecraft.getNetworkProxy());
						httpURLConnection.setDoInput(true);
						httpURLConnection.setDoOutput(false);
						httpURLConnection.connect();
						if (httpURLConnection.getResponseCode() / 100 == 2)
						{
							InputStream inputStream2;
							if (this.cacheFile != null)
							{
								Files.createDirectories(this.cacheFile.getParent());
								Files.copy(httpURLConnection.getInputStream(), this.cacheFile);
								inputStream2 = Files.newInputStream(this.cacheFile);
							}
							else
							{
								inputStream2 = httpURLConnection.getInputStream();
							}

							minecraft.execute(() -> {
								var nativeImage = this.loadTexture(inputStream2);
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
						Lumberjack.error("Couldn't download http texture");
						var6.printStackTrace();
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
			Lumberjack.warn("Error while loading remote texture");
			var4.printStackTrace();
		}

		return nativeImage;
	}
}
