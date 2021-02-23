package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.tag.Tag;

public class SwgTags
{
	public static class Block
	{
		public static final Tag<net.minecraft.block.Block> DESERT_SAND = TagRegistry.block(Resources.identifier("desert_sand"));
	}
}
