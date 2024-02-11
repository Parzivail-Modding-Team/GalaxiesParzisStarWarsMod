package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SwgTags
{
	public static class Blocks
	{
		public static final TagKey<Block> SLIDING_DOORS = register("sliding_doors");
		public static final TagKey<Block> BLASTER_DESTROY = register("blaster_destroy");
		public static final TagKey<Block> BLASTER_EXPLODE = register("blaster_explode");
		public static final TagKey<Block> BLASTER_REFLECT = register("blaster_reflect");

		private static TagKey<Block> register(String id)
		{
			return TagKey.of(RegistryKeys.BLOCK, Resources.id(id));
		}
	}

	public static class Items
	{
		private static TagKey<Item> register(String id)
		{
			return TagKey.of(RegistryKeys.ITEM, Resources.id(id));
		}
	}

	public static class EntityTypes
	{
		public static final TagKey<EntityType<?>> DETONATES_EXPLOSIVE = register("detonates_explosive");

		private static TagKey<EntityType<?>> register(String id)
		{
			return TagKey.of(RegistryKeys.ENTITY_TYPE, Resources.id(id));
		}
	}

	public static class DamageTypes
	{
		public static final TagKey<DamageType> IS_IGNORES_INVULNERABLE_FRAMES = register("is_ignores_invulnerable_frames");
		public static final TagKey<DamageType> IGNITES_EXPLOSIVE = register("ignites_explosive");

		private static TagKey<DamageType> register(String id)
		{
			return TagKey.of(RegistryKeys.DAMAGE_TYPE, Resources.id(id));
		}
	}
}
