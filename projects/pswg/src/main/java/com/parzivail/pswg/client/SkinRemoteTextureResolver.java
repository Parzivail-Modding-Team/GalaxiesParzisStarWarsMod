package com.parzivail.pswg.client;

import com.parzivail.util.client.texture.remote.RemoteTextureResolver;
import com.parzivail.util.client.texture.remote.RemoteTextureUrl;
import net.minecraft.util.Identifier;

public class SkinRemoteTextureResolver implements RemoteTextureResolver
{
	public RemoteTextureUrl getTexture(Identifier id)
	{
		var remotePath = id.getPath();
		var path = remotePath.substring(remotePath.lastIndexOf('/') + 1);
		return new RemoteTextureUrl(String.format("https://pswg.dev/skins/%s.png", path));
	}
}
