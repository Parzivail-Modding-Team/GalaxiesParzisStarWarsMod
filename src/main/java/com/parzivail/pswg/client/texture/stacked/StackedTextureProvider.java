package com.parzivail.pswg.client.texture.stacked;

import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public class StackedTextureProvider
{
	private static final HashMap<String, Identifier> TEXTURE_CACHE = new HashMap<>();

	private final TextureManager textureManager;
	private final String identifierRoot;

	public StackedTextureProvider(TextureManager textureManager, String identifierRoot)
	{
		this.textureManager = textureManager;
		this.identifierRoot = identifierRoot;
	}

	public Identifier loadTexture(String id, Supplier<Identifier> fallback, Collection<Identifier> textures)
	{
		Identifier identifier = getIdentifier(id);
		AbstractTexture texture = textureManager.getTexture(identifier);

		// The texture is fully loaded
		if (texture != null)
			return identifier;

		if (!TEXTURE_CACHE.containsKey(id))
		{
			// The texture hasn't been stacked yet
			loadTexture(identifier, textures);
			TEXTURE_CACHE.put(id, identifier);
		}

		// The texture has been stacked but hasn't been loaded yet
		return fallback.get();
	}

	public void loadTexture(Identifier identifier, Collection<Identifier> textures)
	{
		Util.getMainWorkerExecutor().execute(() -> {
			try
			{
				Client.minecraft.execute(() -> {
					RenderSystem.recordRenderCall(() -> {
						AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
						// TODO: fallback texture
						if (!(abstractTexture instanceof StackedTexture))
						{
							StackedTexture remoteTexture = new StackedTexture(DefaultSkinHelper.getTexture(), textures);
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
