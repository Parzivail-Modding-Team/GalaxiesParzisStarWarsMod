package com.parzivail.pswg.client.texture;

import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.data.FallbackIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class TextureProvider<TData>
{
	private static final ArrayList<Identifier> TEXTURE_CACHE = new ArrayList<>();
	private static final HashMap<Identifier, List<Consumer<Boolean>>> LOAD_CALLBACKS = new HashMap<>();

	public static final ArrayList<TextureProvider<?>> TEXTURE_PROVIDERS = new ArrayList<>();

	private final Identifier root;
	private final TextureManager textureManager;

	protected TextureProvider(Identifier cacheIdRoot, TextureManager textureManager)
	{
		this.root = cacheIdRoot;
		this.textureManager = textureManager;

		TEXTURE_PROVIDERS.add(this);
	}

	@NotNull
	private Identifier createCacheId(String requestName)
	{
		return new Identifier(root.getNamespace(), root.getPath() + "/" + requestName);
	}

	public boolean isProviderFor(Identifier cacheId)
	{
		return cacheId.getNamespace().equals(root.getNamespace()) && cacheId.getPath().startsWith(root.getPath());
	}

	public Identifier getProvidedId(Identifier cacheId)
	{
		// If we provided this ID, return it
		if (isProviderFor(cacheId))
			return cacheId;

		// Also return if we provided the fallback ID in a FallbackIdentifier
		if (cacheId instanceof FallbackIdentifier fi && isProviderFor(fi.getSource()))
			return fi.getSource();

		return null;
	}

	public boolean isReady(Identifier cacheId)
	{
		return TEXTURE_CACHE.contains(cacheId) && textureManager.getOrDefault(cacheId, null) != null;
	}

	protected void markTextureDirty(Identifier cacheId)
	{
		TEXTURE_CACHE.removeIf(cacheId::equals);
	}

	protected void registerDependencyCallbacks(Identifier cacheId, Identifier dependencyCacheId)
	{
		for (var provider : TEXTURE_PROVIDERS)
		{
			var providerCacheId = provider.getProvidedId(dependencyCacheId);

			if (providerCacheId != null && !provider.isReady(dependencyCacheId))
			{
				provider.addLoadCallback(dependencyCacheId, (success) -> {
					if (success)
						markTextureDirty(cacheId);
				});

				Lumberjack.debug("%s -> %s found dependency on %s for %s", this.getClass().getSimpleName(), cacheId, provider.root, dependencyCacheId);
				// There should only be one provider for each cache ID
				break;
			}
		}
	}

	public Identifier getId(String requestName, Supplier<Identifier> fallback, Supplier<TData> requestFulfiller)
	{
		var cacheId = createCacheId(requestName);
		var texture = textureManager.getOrDefault(cacheId, null);

		// The texture is fully loaded and isn't marked as dirty (i.e. the cache contains it)
		if (texture != null && TEXTURE_CACHE.contains(cacheId))
			return cacheId;

		if (!TEXTURE_CACHE.contains(cacheId))
		{
			// The texture hasn't been stacked yet
			bakeTextureAsync(cacheId, requestFulfiller.get());
			TEXTURE_CACHE.add(cacheId);
		}

		// The texture has been stacked but hasn't been loaded yet
		var fallbackId = fallback.get();
		return new FallbackIdentifier(fallbackId.getNamespace(), fallbackId.getPath(), cacheId);
	}

	protected abstract CallbackTexture createTexture(Identifier destId, TData requestData, Consumer<Boolean> callback);

	protected void bakeTextureAsync(Identifier cacheId, TData request)
	{
		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				var minecraft = MinecraftClient.getInstance();
				minecraft.execute(() -> RenderSystem.recordRenderCall(() -> this.textureManager.registerTexture(cacheId, createTexture(cacheId, request, success -> pollCallbacks(cacheId, success)))));
			}
			catch (InsecureTextureException insecureTextureException)
			{
				Lumberjack.warn("Attempt to load insecure texture blocked: %s ", cacheId);
				insecureTextureException.printStackTrace();
			}
		});
	}

	public void addLoadCallback(Identifier target, Consumer<Boolean> callback)
	{
		Lumberjack.debug("%s addLoadCallback", this.getClass().getSimpleName());
		if (!LOAD_CALLBACKS.containsKey(target))
			LOAD_CALLBACKS.put(target, new ArrayList<>());

		LOAD_CALLBACKS.get(target).add(callback);
	}

	private void pollCallbacks(Identifier identifier, boolean success)
	{
		Lumberjack.debug("%s pollCallbacks (success: %s)", this.getClass().getSimpleName(), success);
		var callbacks = LOAD_CALLBACKS.get(identifier);
		if (callbacks == null)
			return;

		callbacks.forEach(callback -> callback.accept(success));
		LOAD_CALLBACKS.remove(identifier);
	}
}
