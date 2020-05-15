package com.parzivail.pswg.json;

import javax.annotation.Nullable;

public class PSWGTextureMeta
{
	@Nullable
	public Layer[] layers = null;

	public static class Layer
	{
		public String texture;
		public int tint = 0xffffffff;
	}
}
