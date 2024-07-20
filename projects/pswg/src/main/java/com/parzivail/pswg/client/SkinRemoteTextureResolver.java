package com.parzivail.pswg.client;

import com.parzivail.util.client.texture.remote.RemoteTextureResolver;
import com.parzivail.util.client.texture.remote.RemoteTextureUrl;
import net.minecraft.util.Identifier;

public class SkinRemoteTextureResolver implements RemoteTextureResolver
{
	@Override
	public RemoteTextureUrl getTexture(Identifier id)
	{
		var remotePath = id.getPath();
		var path = remotePath.substring(remotePath.lastIndexOf('/') + 1);
		return new RemoteTextureUrl("https://pswg.dev/skins/" + path + ".png");
	}
}
