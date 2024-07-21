package com.parzivail.util.client.texture.remote;

import com.parzivail.pswg.Galaxies;
import com.parzivail.util.ParziUtil;
import com.parzivail.util.client.texture.CallbackTexture;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class RemoteTexture extends CallbackTexture
{
	private final Identifier remoteId;
	@Nullable
	private final Path cacheFile;
	private final String url;
	@Nullable
	private CompletableFuture<?> loader;

	public RemoteTexture(Identifier remoteId, @Nullable Path cacheFile, String url, Identifier fallbackSkin, BooleanConsumer onResolved)
	{
		super(fallbackSkin, onResolved);
		this.remoteId = remoteId;
		this.cacheFile = cacheFile;
		this.url = url;
	}

	@Override
	public void load(ResourceManager manager) throws IOException
	{
		MinecraftClient.getInstance().execute(() -> {
			if (!this.isLoaded)
			{
				try
				{
					super.load(manager);
				}
				catch (IOException var3)
				{
					ParziUtil.LOG.warn("Failed to load texture: %s", this.location);
					var3.printStackTrace();
				}

				this.isLoaded = true;
			}
		});

		generateImage(manager);
	}

	@Override
	protected NativeImage generateImage(ResourceManager manager) throws IOException
	{
		var minecraft = MinecraftClient.getInstance();
		if (this.loader == null)
		{
			NativeImage nativeImage2;
			if (this.cacheFile != null && Files.isRegularFile(cacheFile))
			{
				var lastModifiedTime = Files.getLastModifiedTime(cacheFile);
				var cacheFileAge = lastModifiedTime.toInstant().until(Instant.now(), ChronoUnit.MINUTES);

				if (cacheFileAge > 30)
				{
					ParziUtil.LOG.debug("Locally cached http texture too old (%s, age=%s min)", this.cacheFile, cacheFileAge);
					// Allow file to regenerate if it still exists on the remote
					Files.delete(cacheFile);
					nativeImage2 = null;
				}
				else
				{
					ParziUtil.LOG.debug("Loading http texture from local cache (%s)", this.cacheFile);
					var inputStream = Files.newInputStream(this.cacheFile);
					nativeImage2 = this.readImage(inputStream);
				}
			}
			else
			{
				nativeImage2 = null;
			}

			if (nativeImage2 != null)
			{
				this.complete(nativeImage2);
			}
			else
			{
				this.loader = CompletableFuture.runAsync(() -> {
					HttpURLConnection httpURLConnection = null;
					Galaxies.LOG.debug("Downloading http texture from %s to %s", this.url, this.cacheFile);

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
								var nativeImage = this.readImage(inputStream2);
								if (nativeImage != null)
								{
									this.complete(nativeImage);
								}
							});
							return;
						}
						else
						{
							Galaxies.LOG.debug("No skin found on remote");

							minecraft.execute(() -> {
								this.complete(null);
							});
						}
					}
					catch (Exception var6)
					{
						Galaxies.LOG.error("Couldn't download http texture");
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
		return null;
	}

	@Nullable
	private NativeImage readImage(InputStream stream)
	{
		NativeImage nativeImage = null;

		try
		{
			nativeImage = NativeImage.read(stream);
		}
		catch (IOException var4)
		{
			Galaxies.LOG.warn("Error while loading remote texture");
			var4.printStackTrace();
		}

		return nativeImage;
	}
}
