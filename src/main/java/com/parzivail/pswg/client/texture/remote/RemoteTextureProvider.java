package com.parzivail.pswg.client.texture.remote;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.data.FallbackIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RemoteTextureProvider
{
	private static final HashMap<String, Identifier> TEXTURE_CACHE = new HashMap<>();
	private static final HashMap<Identifier, List<Runnable>> LOAD_CALLBACKS = new HashMap<>();

	private final TextureManager textureManager;
	private final String identifierRoot;
	private final Path skinCacheDir;
	private final RemoteTextureResolver remoteTextureResolver;

	public RemoteTextureProvider(TextureManager textureManager, String identifierRoot, Path skinCacheDir)
	{
		this.textureManager = textureManager;
		this.identifierRoot = identifierRoot;
		this.skinCacheDir = skinCacheDir;
		remoteTextureResolver = new RemoteTextureResolver();
	}

	public boolean isRemoteTexture(Identifier id)
	{
		return id.toString().startsWith(identifierRoot);
	}

	public Identifier getRemoteTextureId(Identifier id)
	{
		if (isRemoteTexture(id))
			return id;

		if (id instanceof FallbackIdentifier fi && isRemoteTexture(fi.getSource()))
			return fi.getSource();

		return null;
	}

	public Identifier loadTexture(String id, Supplier<Identifier> fallback)
	{
		var identifier = getIdentifier(id);
		var texture = textureManager.getOrDefault(identifier, null);

		// The texture is fully loaded
		if (texture != null)
			return identifier;

		if (!TEXTURE_CACHE.containsKey(id))
		{
			// The texture hasn't been resolved yet
			loadTexture(identifier);
			TEXTURE_CACHE.put(id, identifier);
		}

		// The texture has been resolved but hasn't been loaded yet
		var fallbackId = fallback.get();
		return new FallbackIdentifier(fallbackId.getNamespace(), fallbackId.getPath(), identifier);
	}

	public void loadTexture(Identifier identifier)
	{
		// Note: `identifier` may or may not be a URL, that's up to `remoteTextureResolver` to resolve

		var minecraft = MinecraftClient.getInstance();

		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				final var remoteTextureUrl = remoteTextureResolver.getTexture(identifier);

				minecraft.execute(() -> RenderSystem.recordRenderCall(() -> {
					String string = Hashing.sha1().hashUnencodedChars(remoteTextureUrl.getHash()).toString();

					Path path = this.skinCacheDir.resolve(string.length() > 2 ? string.substring(0, 2) : "xx");
					Path path2 = path.resolve(string);
					RemoteTexture remoteTexture = new RemoteTexture(path2, remoteTextureUrl.getUrl(), DefaultSkinHelper.getTexture(), () -> {
						pollCallbacks(identifier);
					});
					this.textureManager.registerTexture(identifier, remoteTexture);
				}));
			}
			catch (InsecureTextureException var7)
			{
			}
		});
	}

	public void addLoadCallback(Identifier target, Runnable callback)
	{
		if (!LOAD_CALLBACKS.containsKey(target))
			LOAD_CALLBACKS.put(target, new ArrayList<>());

		LOAD_CALLBACKS.get(target).add(callback);
	}

	private void pollCallbacks(Identifier identifier)
	{
		var callbacks = LOAD_CALLBACKS.get(identifier);
		if (callbacks != null)
		{
			callbacks.forEach(Runnable::run);
			LOAD_CALLBACKS.remove(identifier);
		}
	}

	@NotNull
	private Identifier getIdentifier(String id)
	{
		return new Identifier(identifierRoot + "/" + id);
	}
}
