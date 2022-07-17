package com.parzivail.util.client.texture.tinted;

import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.client.texture.TextureProvider;
import com.parzivail.util.data.TintedIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TintedTextureProvider extends TextureProvider<TintedIdentifier>
{
	public TintedTextureProvider(Identifier cacheIdRoot, TextureManager textureManager)
	{
		super(cacheIdRoot, textureManager);
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, TintedIdentifier request, Consumer<Boolean> callback)
	{
		registerDependencyCallbacks(destId, request);
		return new TintedTexture(request, DefaultSkinHelper.getTexture(), callback);
	}
}
