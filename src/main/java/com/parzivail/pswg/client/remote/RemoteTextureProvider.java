package com.parzivail.pswg.client.remote;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class RemoteTextureProvider
{
	private final TextureManager textureManager;
	private final String identifierRoot;
	private final File skinCacheDir;
	private final RemoteTextureResolver remoteTextureResolver;
	private final LoadingCache<String, RemoteTextureUrl> skinCache;

	public RemoteTextureProvider(TextureManager textureManager, String identifierRoot, File skinCacheDir)
	{
		this.textureManager = textureManager;
		this.identifierRoot = identifierRoot;
		this.skinCacheDir = skinCacheDir;
		remoteTextureResolver = new RemoteTextureResolver();
		this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, RemoteTextureUrl>()
		{
			public RemoteTextureUrl load(String string)
			{
				try
				{
					return remoteTextureResolver.getTexture(string);
				}
				catch (Throwable var4)
				{
					return null;
				}
			}
		});
	}

	public Identifier loadTexture(String id)
	{
		final Identifier identifier = new Identifier(identifierRoot + "/" + id);

		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				final RemoteTextureUrl remoteTextureUrl = this.skinCache.getUnchecked(id);

				MinecraftClient.getInstance().execute(() -> {
					RenderSystem.recordRenderCall(() -> {
						String string = Hashing.sha1().hashUnencodedChars(remoteTextureUrl.getHash()).toString();
						AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
						// TODO: fallback texture
						if (!(abstractTexture instanceof RemoteTexture))
						{
							File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
							File file2 = new File(file, string);
							RemoteTexture remoteTexture = new RemoteTexture(file2, remoteTextureUrl.getUrl(), DefaultSkinHelper.getTexture());
							this.textureManager.registerTexture(identifier, remoteTexture);
						}
					});
				});
			}
			catch (InsecureTextureException var7)
			{
			}
		});

		return identifier;
	}
}
