package dev.pswg.entity;

import dev.pswg.Blasters;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Temporary blaster disable states. Native effects restore attributes on expiry.
 */
public final class BlasterStatusEffects
{
	/**
	 * Stops movement and clears a mob's current attack while active.
	 */
	private static final class DisabledEffect extends MobEffect
	{
		/**
		 * Creates one native effect with its own attribute IDs.
		 */
		private DisabledEffect(String name, int color)
		{
			super(MobEffectCategory.HARMFUL, color);
			addAttributeModifier(Attributes.MOVEMENT_SPEED, Blasters.id(name + "_movement"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
			addAttributeModifier(Attributes.ATTACK_DAMAGE, Blasters.id(name + "_attack"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		}

		/**
		 * Runs every active server tick.
		 */
		@Override
		public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
		{
			return true;
		}

		/**
		 * Stops current mob navigation without changing its permanent AI flag.
		 */
		@Override
		public boolean applyEffectTick(ServerLevel world, LivingEntity target, int amplifier)
		{
			if (target instanceof Mob mob)
			{
				mob.getNavigation().stop();
				mob.setTarget(null);
			}
			return true;
		}
	}

	/**
	 * Checks the native disable states used by blaster eligibility.
	 */
	public static boolean disabled(LivingEntity entity)
	{
		return entity.hasEffect(STUNNED) || entity.hasEffect(ION_DISRUPTION);
	}

	public static void register()
	{
	}

	/**
	 * Nonlethal stun.
	 */
	public static final Holder<MobEffect> STUNNED = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			Blasters.id("stunned"),
			new DisabledEffect("stunned", 0x65B9FF)
	);

	/**
	 * Disable state for tagged ion targets.
	 */
	public static final Holder<MobEffect> ION_DISRUPTION = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			Blasters.id("ion_disruption"),
			new DisabledEffect("ion", 0x85D8EF)
	);

	private BlasterStatusEffects()
	{
	}
}
