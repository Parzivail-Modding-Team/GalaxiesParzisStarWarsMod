package com.parzivail.pswg.json;

import javax.annotation.Nullable;
import java.util.List;

public class PSWGTextureMeta
{
	@Nullable
	public List<Layer> layers = null;

	public static class Layer {
		public String layer;
		public int tint = 0xffffff;
	}
}
