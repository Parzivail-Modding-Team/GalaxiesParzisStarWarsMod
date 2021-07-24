package com.parzivail.pswg.client.texture.stacked;

import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.client.texture.remote.RemoteTexture;
import com.parzivail.util.data.RemoteFallbackIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public record StackedTextureProvider(TextureManager textureManager,
                                     String identifierRoot)
{
	private static final HashMap<String, Identifier> TEXTURE_CACHE = new HashMap<>();

	public Identifier loadTexture(String id, Supplier<Identifier> fallback, Supplier<Collection<Identifier>> textures)
	{
		var identifier = getIdentifier(id);
		var texture = textureManager.getOrDefault(identifier, null);

		// The texture is fully loaded
		if (texture != null)
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

	private void onRemoteTextureResolved(RemoteTexture remoteTexture)
	{

	}

	public void loadTexture(Identifier identifier, Collection<Identifier> textures)
	{
		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				var minecraft = MinecraftClient.getInstance();
				minecraft.execute(() -> RenderSystem.recordRenderCall(() -> {
					AbstractTexture abstractTexture = this.textureManager.getOrDefault(identifier, null);
					if (!(abstractTexture instanceof StackedTexture))
					{
						for (var id : textures)
						{
							if (id instanceof RemoteFallbackIdentifier rfi)
								rfi.addCallback(() -> {
									TEXTURE_CACHE.values().removeIf(identifier::equals);
								});
						}

						StackedTexture texture = new StackedTexture(identifier, DefaultSkinHelper.getTexture(), textures);
						this.textureManager.registerTexture(identifier, texture);
					}
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
