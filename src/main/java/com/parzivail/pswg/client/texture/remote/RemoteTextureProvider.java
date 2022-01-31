package com.parzivail.pswg.client.texture.remote;

import com.google.common.hash.Hashing;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.client.texture.CallbackTexture;
import com.parzivail.pswg.client.texture.TextureProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RemoteTextureProvider extends TextureProvider<Identifier>
{
	private final Path skinCacheDir;
	private final RemoteTextureResolver remoteTextureResolver;

	public RemoteTextureProvider(Identifier cacheIdRoot, TextureManager textureManager, Path skinCacheDir)
	{
		super(cacheIdRoot, textureManager);
		this.skinCacheDir = skinCacheDir;
		remoteTextureResolver = new RemoteTextureResolver();
	}

	public Identifier getId(String requestName, Supplier<Identifier> fallback)
	{
		return getId(requestName, fallback, () -> createCacheId(requestName));
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, Identifier requestData, Consumer<Boolean> callback)
	{
		registerDependencyCallbacks(destId, requestData);

		final var remoteTextureUrl = remoteTextureResolver.getTexture(requestData);

		var string = Hashing.sha1().hashUnencodedChars(remoteTextureUrl.getHash()).toString();

		var skinCacheDir = this.skinCacheDir.resolve(string.length() > 2 ? string.substring(0, 2) : "xx");
		var skinCacheFile = skinCacheDir.resolve(string);
		return new RemoteTexture(requestData, skinCacheFile, remoteTextureUrl.getUrl(), Client.TEX_TRANSPARENT, success -> {
			if (!success)
				markTextureFailure(requestData);
			callback.accept(success);
		});
	}
}
