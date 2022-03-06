package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;

public class SwgTags
{
	public static class Block
	{
		public static final TagKey<net.minecraft.block.Block> DESERT_SAND = TagFactory.BLOCK.create(Resources.id("desert_sand"));
		public static final TagKey<net.minecraft.block.Block> DESERT_SANDSTONE = TagFactory.BLOCK.create(Resources.id("desert_sandstone"));
		public static final TagKey<net.minecraft.block.Block> JAPOR_LOG = TagFactory.BLOCK.create(Resources.id("japor_log"));
		public static final TagKey<net.minecraft.block.Block> SEQUOIA_LOG = TagFactory.BLOCK.create(Resources.id("sequoia_log"));
		public static final TagKey<net.minecraft.block.Block> TATOOINE_DOORS = TagFactory.BLOCK.create(Resources.id("tatooine_doors"));
		public static final TagKey<net.minecraft.block.Block> TATOOINE_LOG = TagFactory.BLOCK.create(Resources.id("tatooine_log"));
	}

	public static class Item
	{
		public static final TagKey<net.minecraft.item.Item> DESERT_SAND = TagFactory.ITEM.create(Resources.id("desert_sand"));
		public static final TagKey<net.minecraft.item.Item> DESERT_SANDSTONE = TagFactory.ITEM.create(Resources.id("desert_sandstone"));
		public static final TagKey<net.minecraft.item.Item> JAPOR_LOG = TagFactory.ITEM.create(Resources.id("japor_log"));
		public static final TagKey<net.minecraft.item.Item> SEQUOIA_LOG = TagFactory.ITEM.create(Resources.id("sequoia_log"));
		public static final TagKey<net.minecraft.item.Item> TATOOINE_LOG = TagFactory.ITEM.create(Resources.id("tatooine_log"));
	}
}
