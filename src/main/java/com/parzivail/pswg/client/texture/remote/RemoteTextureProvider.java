package com.parzivail.pswg.client.texture.remote;

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
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RemoteTextureProvider
{
	private static final HashMap<String, Identifier> TEXTURE_CACHE = new HashMap<>();

	private final TextureManager textureManager;
	private final String identifierRoot;
	private final Path skinCacheDir;
	private final RemoteTextureResolver remoteTextureResolver;
	private final LoadingCache<Identifier, RemoteTextureUrl> skinCache;

	public RemoteTextureProvider(TextureManager textureManager, String identifierRoot, Path skinCacheDir)
	{
		this.textureManager = textureManager;
		this.identifierRoot = identifierRoot;
		this.skinCacheDir = skinCacheDir;
		remoteTextureResolver = new RemoteTextureResolver();
		this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<Identifier, RemoteTextureUrl>()
		{
			public RemoteTextureUrl load(Identifier id)
			{
				try
				{
					return remoteTextureResolver.getTexture(id);
				}
				catch (Throwable var4)
				{
					return null;
				}
			}
		});
	}

	public Identifier loadTexture(String id, Supplier<Identifier> fallback)
	{
		Identifier identifier = getIdentifier(id);
		AbstractTexture texture = textureManager.getTexture(identifier);

		// The texture is fully loaded
		if (texture != null)
			return identifier;

		if (!TEXTURE_CACHE.containsKey(id))
		{
			// The texture hasn't been stacked yet
			loadTexture(identifier);
			TEXTURE_CACHE.put(id, identifier);
		}

		// The texture has been stacked but hasn't been loaded yet
		return fallback.get();
	}

	public void loadTexture(Identifier identifier)
	{
		// Note: `identifier` may or may not be a URL, that's up to `remoteTextureResolver` to resolve

		MinecraftClient minecraft = MinecraftClient.getInstance();

		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				final RemoteTextureUrl remoteTextureUrl = this.skinCache.getUnchecked(identifier);

				minecraft.execute(() -> {
					RenderSystem.recordRenderCall(() -> {
						String string = Hashing.sha1().hashUnencodedChars(remoteTextureUrl.getHash()).toString();
						AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
						if (!(abstractTexture instanceof RemoteTexture))
						{
							Path path = this.skinCacheDir.resolve(string.length() > 2 ? string.substring(0, 2) : "xx");
							Path path2 = path.resolve(string);
							RemoteTexture remoteTexture = new RemoteTexture(path2, remoteTextureUrl.getUrl(), DefaultSkinHelper.getTexture());
							this.textureManager.registerTexture(identifier, remoteTexture);
						}
					});
				});
			}
			catch (InsecureTextureException var7)
			{
			}
		});
	}

	@NotNull
	private Identifier getIdentifier(String id)
	{
		return new Identifier(identifierRoot + "/" + id);
	}
}
