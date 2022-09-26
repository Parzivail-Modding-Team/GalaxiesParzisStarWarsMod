package com.parzivail.util.client.texture.stacked;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.client.texture.TextureProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class StackedTextureProvider extends TextureProvider<Collection<Identifier>>
{
	public static final Identifier ROOT = Resources.id("///stacked");

	private final Identifier transparentTexture;

	public StackedTextureProvider(TextureManager textureManager, Identifier transparentTexture)
	{
		super(ROOT, textureManager);
		this.transparentTexture = transparentTexture;
	}

	@NotNull
	public static Identifier getCacheId(String requestName)
	{
		return new Identifier(ROOT.getNamespace(), ROOT.getPath() + "/" + requestName);
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, Collection<Identifier> requestData, Consumer<Boolean> callback)
	{
		for (var id : requestData)
			registerDependencyCallbacks(destId, id);
		return new StackedTexture(transparentTexture, DefaultSkinHelper.getTexture(), requestData, callback);
	}
}
