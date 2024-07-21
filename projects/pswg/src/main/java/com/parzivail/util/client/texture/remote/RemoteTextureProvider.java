package com.parzivail.util.client.texture.remote;

import com.google.common.hash.Hashing;
import com.parzivail.pswg.Resources;
import com.parzivail.util.client.texture.CallbackTexture;
import com.parzivail.util.client.texture.TextureProvider;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class RemoteTextureProvider extends TextureProvider<Identifier>
{
	public static final Identifier ROOT = Resources.id("///remote_skin");

	private final Identifier transparentTexture;
	private final Path skinCacheDir;
	private final RemoteTextureResolver remoteTextureResolver;

	public RemoteTextureProvider(TextureManager textureManager, Identifier transparentTexture, RemoteTextureResolver remoteTextureResolver, Path skinCacheDir)
	{
		super(ROOT, textureManager);
		this.transparentTexture = transparentTexture;
		this.skinCacheDir = skinCacheDir;
		this.remoteTextureResolver = remoteTextureResolver;
	}

	public Identifier getId(String requestName, Supplier<Identifier> fallback)
	{
		return getId(requestName, fallback, () -> createCacheId(requestName));
	}

	@Override
	protected CallbackTexture createTexture(Identifier destId, Identifier requestData, BooleanConsumer callback)
	{
		registerDependencyCallbacks(destId, requestData);

		final var remoteTextureUrl = remoteTextureResolver.getTexture(requestData);

		var string = Hashing.sha1().hashUnencodedChars(remoteTextureUrl.getHash()).toString();

		var skinCacheDir = this.skinCacheDir.resolve(string.length() > 2 ? string.substring(0, 2) : "xx");
		var skinCacheFile = skinCacheDir.resolve(string);
		return new RemoteTexture(requestData, skinCacheFile, remoteTextureUrl.getUrl(), transparentTexture, success -> {
			if (!success)
				markTextureFailure(requestData);
			callback.accept(success);
		});
	}
}
