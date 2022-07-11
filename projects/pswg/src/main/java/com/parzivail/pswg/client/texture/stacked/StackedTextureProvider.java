package com.parzivail.pswg.client.texture.stacked;

import com.parzivail.pswg.client.texture.CallbackTexture;
import com.parzivail.pswg.client.texture.TextureProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class StackedTextureProvider extends TextureProvider<Collection<Identifier>>
{
	public StackedTextureProvider(Identifier cacheIdRoot, TextureManager textureManager)
	{
		super(cacheIdRoot, textureManager);
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, Collection<Identifier> requestData, Consumer<Boolean> callback)
	{
		for (var id : requestData)
			registerDependencyCallbacks(destId, id);
		return new StackedTexture(DefaultSkinHelper.getTexture(), requestData, callback);
	}
}
