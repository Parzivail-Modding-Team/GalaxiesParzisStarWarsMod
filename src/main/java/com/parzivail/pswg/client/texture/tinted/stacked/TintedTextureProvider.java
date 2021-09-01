package com.parzivail.pswg.client.texture.tinted.stacked;

import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.util.data.TintedIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public record TintedTextureProvider(TextureManager textureManager,
                                    String identifierRoot)
{
	private static final HashMap<String, Identifier> TEXTURE_CACHE = new HashMap<>();

	public Identifier loadTexture(String id, Supplier<Identifier> fallback, Supplier<TintedIdentifier> textures)
	{
		var identifier = getIdentifier(id);
		var texture = textureManager.getOrDefault(identifier, null);

		// The texture is fully loaded and isn't marked as dirty (i.e. the cache contains it)
		if (texture != null && TEXTURE_CACHE.containsKey(id))
			return identifier;

		if (!TEXTURE_CACHE.containsKey(id))
		{
			// The texture hasn't been stacked yet
			loadTexture(identifier, textures.get());
			TEXTURE_CACHE.put(id, identifier);
		}

		// The texture has been stacked but hasn't been loaded yet
		return fallback.get();
	}

	private void markTextureDirty(Identifier identifier)
	{
		var size = TEXTURE_CACHE.size();
		TEXTURE_CACHE.values().removeIf(identifier::equals);
	}

	public void loadTexture(Identifier identifier, TintedIdentifier textureId)
	{
		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				var minecraft = MinecraftClient.getInstance();
				minecraft.execute(() -> RenderSystem.recordRenderCall(() -> {

					Identifier remoteId = Client.remoteTextureProvider.getRemoteTextureId(textureId);
					if (remoteId != null)
						Client.remoteTextureProvider.addLoadCallback(remoteId, () -> {
							markTextureDirty(identifier);
						});

					TintedTexture texture = new TintedTexture(identifier, DefaultSkinHelper.getTexture(), textureId);
					this.textureManager.registerTexture(identifier, texture);
				}));
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
