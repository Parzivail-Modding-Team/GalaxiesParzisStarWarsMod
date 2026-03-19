package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.effects.IntoxicatedEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class GadgetsDamage
{
	public static class DamageTags
	{
		public static final TagKey<DamageType> IGNITES_EXPLOSIVES = TagKey.create(Registries.DAMAGE_TYPE, Gadgets.id("ignites_explosives"));

		public static void register()
		{
		}
	}

	public static final ResourceKey<DamageType> NERVE_GAS_DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, Gadgets.id("nerve_gas"));

	public static void register()
	{

	}

	public static DamageSource create(Level world, ResourceKey<DamageType> key)
	{
		return new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
	}
}
