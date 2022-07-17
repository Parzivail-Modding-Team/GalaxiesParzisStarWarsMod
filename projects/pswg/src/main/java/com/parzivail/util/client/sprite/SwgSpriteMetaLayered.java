package com.parzivail.util.client.sprite;

import javax.annotation.Nullable;

public class SwgSpriteMetaLayered
{
	@Nullable
	public Layer[] layers = null;

	public static class Layer
	{
		public String texture;
		public int tint = 0xffffffff;
	}
}
