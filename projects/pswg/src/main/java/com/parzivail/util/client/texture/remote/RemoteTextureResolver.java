package com.parzivail.util.client.texture.remote;

import net.minecraft.util.Identifier;

public interface RemoteTextureResolver
{
	RemoteTextureUrl getTexture(Identifier id);
}
