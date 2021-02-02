package com.parzivail.pswg.client.texture.remote;

import net.minecraft.util.Identifier;

public class RemoteTextureResolver
{
	public RemoteTextureUrl getTexture(Identifier id)
	{
		return new RemoteTextureUrl("https://raw.githubusercontent.com/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod/master/resources/images/logo_micro_yellow.png");
	}
}
