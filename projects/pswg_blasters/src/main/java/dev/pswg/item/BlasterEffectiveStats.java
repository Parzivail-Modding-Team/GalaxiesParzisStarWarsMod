package dev.pswg.item;

import dev.pswg.data.BlasterAttachmentDefinition.Modifier;
import dev.pswg.data.BlasterAttachmentDefinition.ModifierCondition;
import dev.pswg.data.BlasterStanceProfile.WeaponState;
import dev.pswg.data.BlasterStatFunction;
import dev.pswg.data.BlasterStats;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shared statistics for gameplay and presentation.
 *
 * @param stats            Effective stats.
 * @param zoom             Actual aiming zoom, relative to the native attribute's unmodified base value of one.
 * @param recoilMultiplier Presentation recoil factor for the retained prototype kick.
 */
public record BlasterEffectiveStats(BlasterStats stats, float zoom, float recoilMultiplier)
{
	/**
	 * Caller-owned runtime/preview context; mode and archetype are taken from the loadout itself.
	 */
	public record Context(WeaponState stance, boolean deployed, boolean ads, boolean folded)
	{
	}

	/**
	 * Stable source identity for a matching modifier row.
	 */
	private record AppliedModifier(Identifier attachmentId, int row, Modifier modifier)
	{
	}

	/**
	 * Evaluates the statistics from the loadout.
	 */
	public static BlasterEffectiveStats evaluate(BlasterLoadout loadout, Context context)
	{
		var base = loadout.definition().stats();
		var rows = new ArrayList<AppliedModifier>();

		for (var entry : loadout.activeOptions().entrySet())
		{
			var modifiers = entry.getValue().modifiers();
			for (var row = 0; row < modifiers.size(); row++)
			{
				var modifier = modifiers.get(row);
				if (matches(modifier.modifierCondition(), loadout, context))
					rows.add(new AppliedModifier(entry.getKey(), row, modifier));
			}
		}

		rows.sort(Comparator.comparingInt((AppliedModifier row) -> row.modifier().priority())
		                    .thenComparing(AppliedModifier::attachmentId).thenComparingInt(AppliedModifier::row));

		var recoil = factor(BlasterStatFunction.RECOIL_MULTIPLIER, rows);
		var spread = factor(BlasterStatFunction.SPREAD_MULTIPLIER, rows);
		var cooling = factor(BlasterStatFunction.COOLING_MULTIPLIER, rows);
		var fireRate = factor(BlasterStatFunction.FIRE_RATE_MULTIPLIER, rows);
		var damageRange = factor(BlasterStatFunction.DAMAGE_RANGE_MULTIPLIER, rows);
		var zoom = factor(BlasterStatFunction.ZOOM_MULTIPLIER, rows);
		var heat = base.heat();
		var aimRecoil = base.recoil();
		var cone = base.spread();
		var recoilPattern = aimRecoil.pattern();
		var recoilPatternPriority = Integer.MIN_VALUE;
		var recoilPatternOption = (Identifier)null;

		for (var option : loadout.activeOptions().entrySet())
		{
			var override = option.getValue().recoilPattern();
			if (override.isEmpty())
				continue;

			var candidate = override.orElseThrow();
			if (candidate.priority() > recoilPatternPriority
			    || (candidate.priority() == recoilPatternPriority && (recoilPatternOption == null || option.getKey().compareTo(recoilPatternOption) > 0)))
			{
				recoilPattern = candidate;
				recoilPatternPriority = candidate.priority();
				recoilPatternOption = option.getKey();
			}
		}

		var effective = base
				.withAutomaticRepeatDelay((int)Math.clamp(Math.ceil(base.automaticRepeatDelay() / fireRate), 1, Integer.MAX_VALUE))
				.withDamageRange(scaled(base.damageRange(), damageRange, base.range()))
				.withHeat(new BlasterStats.HeatDefinition(
						heat.capacity(),
						heat.perRound(),
						scaled(heat.drainSpeed(), cooling, Float.MAX_VALUE),
						heat.overheatPenalty(),
						scaled(heat.overheatDrainSpeed(), cooling, Float.MAX_VALUE),
						heat.passiveCooldownDelay(),
						heat.overchargeBonus()
				))
				.withRecoil(new BlasterStats.Recoil(
						scaled(aimRecoil.hipPitchDegrees(), recoil, 90),
						scaled(aimRecoil.hipYawDegrees(), recoil, 90),
						scaled(aimRecoil.aimPitchDegrees(), recoil, 90),
						scaled(aimRecoil.aimYawDegrees(), recoil, 90),
						aimRecoil.recoveryTicks(),
						recoilPattern
				))
				.withSpread(new BlasterStats.Spread(
						scaled(cone.hipDegrees(), spread, 90),
						scaled(cone.aimDegrees(), spread, 90),
						cone.movingMultiplier(),
						cone.sprintingMultiplier()
				));

		return new BlasterEffectiveStats(
				effective,
				(float)Math.clamp(DEFAULT_ZOOM * zoom, 1, Float.MAX_VALUE),
				(float)Math.min(recoil, Float.MAX_VALUE)
		);
	}

	/**
	 * ANDs supported axes; empty lists and absent booleans are wildcards.
	 */
	private static boolean matches(ModifierCondition condition, BlasterLoadout loadout, Context context)
	{
		return (condition.mode().isEmpty() || condition.mode().contains(loadout.selectedMode().id()))
		       && (condition.archetype().isEmpty() || condition.archetype().contains(loadout.definition().stats().configuration().archetype()))
		       && (condition.stance().isEmpty() || condition.stance().contains(context.stance()))
		       && condition.deployed().map(required -> required == context.deployed()).orElse(true)
		       && condition.folded().map(required -> required == context.folded()).orElse(true)
		       && condition.ads().map(required -> required == context.ads()).orElse(true);
	}

	/**
	 * Composes a unit-base factor with add, multiplied-base and literal-total operation groups.
	 */
	private static double factor(BlasterStatFunction function, List<AppliedModifier> rows)
	{
		var add = 0.0;
		var multipliedBase = 0.0;
		var total = 1.0;
		var zero = false;

		for (var row : rows)
		{
			var modifier = row.modifier();
			if (modifier.function() != function)
				continue;

			switch (modifier.operation())
			{
				case ADD -> add += modifier.value();
				case ADD_MULTIPLIED_BASE -> multipliedBase += modifier.value();
				case MULTIPLY_TOTAL ->
				{
					zero |= modifier.value() == 0;
					total *= modifier.value();
				}
			}
		}

		if (zero)
			return 0;

		return Math.max(Double.MIN_VALUE, (1 + add) * (1 + multipliedBase) * total);
	}

	/**
	 * Saturates a nonnegative numeric field once, preserving zero even when its factor overflows.
	 */
	private static float scaled(float base, double factor, float maximum)
	{
		return base == 0 ? 0 : (float)Math.min(base * factor, maximum);
	}

	/**
	 * Existing aiming attribute's base-one result with its +2 multiplied-base modifier.
	 */
	public static final float DEFAULT_ZOOM = 3;

	/**
	 * Gets the damage multiplier at a certain distance
	 */
	public float damageMultiplierAt(float distance)
	{
		var fraction = stats.damageRange() == 0 ? 1 : Math.clamp(distance / stats.damageRange(), 0, 1);
		var points = stats.falloff();

		for (var index = 1; index < points.size(); index++)
		{
			var right = points.get(index);
			if (fraction <= right.distanceFraction())
			{
				var left = points.get(index - 1);
				var alpha = (fraction - left.distanceFraction()) / (right.distanceFraction() - left.distanceFraction());

				return Mth.lerp(alpha, left.multiplier(), right.multiplier());
			}
		}

		return points.getLast().multiplier();
	}
}
