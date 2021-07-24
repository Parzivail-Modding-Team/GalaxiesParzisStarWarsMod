package com.parzivail.pswg.client.texture.remote;

import net.minecraft.util.Identifier;

public class RemoteTextureResolver
{
	public RemoteTextureUrl getTexture(Identifier id)
	{
		var remotePath = id.getPath();
		var path = remotePath.split("/", 2);
		return new RemoteTextureUrl(String.format("https://pswg.dev/skins/%s.png", path[1]));
	}
}
