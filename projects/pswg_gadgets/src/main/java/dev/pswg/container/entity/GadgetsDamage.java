package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.effects.IntoxicatedEffect;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;

public class GadgetsDamage
{
	public static class DamageTags
	{
		public static final TagKey<DamageType> IGNITES_EXPLOSIVES = TagKey.of(RegistryKeys.DAMAGE_TYPE, Gadgets.id("ignites_explosives"));

		public static void register()
		{
		}
	}

	public static final RegistryKey<DamageType> NERVE_GAS_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Gadgets.id("nerve_gas"));

	public static void register()
	{

	}

	public static DamageSource create(World world, RegistryKey<DamageType> key)
	{
		return new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(key));
	}
}
