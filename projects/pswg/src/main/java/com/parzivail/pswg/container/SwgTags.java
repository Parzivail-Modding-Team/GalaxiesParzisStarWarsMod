package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SwgTags
{
	public static class Block
	{
		public static final TagKey<net.minecraft.block.Block> DESERT_SAND = register("desert_sand");
		public static final TagKey<net.minecraft.block.Block> DESERT_SANDSTONE = register("desert_sandstone");
		public static final TagKey<net.minecraft.block.Block> JAPOR_LOG = register("japor_log");
		public static final TagKey<net.minecraft.block.Block> SEQUOIA_LOG = register("sequoia_log");
		public static final TagKey<net.minecraft.block.Block> SLIDING_DOORS = register("sliding_doors");
		public static final TagKey<net.minecraft.block.Block> TATOOINE_LOG = register("tatooine_log");

		private static TagKey<net.minecraft.block.Block> register(String id)
		{
			return TagKey.of(RegistryKeys.BLOCK, Resources.id(id));
		}
	}

	public static class Item
	{
		public static final TagKey<net.minecraft.item.Item> DESERT_SAND = register("desert_sand");
		public static final TagKey<net.minecraft.item.Item> DESERT_SANDSTONE = register("desert_sandstone");
		public static final TagKey<net.minecraft.item.Item> JAPOR_LOG = register("japor_log");
		public static final TagKey<net.minecraft.item.Item> SEQUOIA_LOG = register("sequoia_log");
		public static final TagKey<net.minecraft.item.Item> TATOOINE_LOG = register("tatooine_log");

		private static TagKey<net.minecraft.item.Item> register(String id)
		{
			return TagKey.of(RegistryKeys.ITEM, Resources.id(id));
		}
	}

	public static class DamageType
	{
		public static final TagKey<net.minecraft.entity.damage.DamageType> IS_IGNORES_INVULNERABLE_FRAMES = register("is_ignores_invulnerable_frames");

		private static TagKey<net.minecraft.entity.damage.DamageType> register(String id)
		{
			return TagKey.of(RegistryKeys.DAMAGE_TYPE, Resources.id(id));
		}
	}
}
