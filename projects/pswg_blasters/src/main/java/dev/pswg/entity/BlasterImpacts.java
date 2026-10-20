package dev.pswg.entity;

import dev.pswg.data.BlasterAreaEffect;
import dev.pswg.data.BlasterExplosionEffect;
import dev.pswg.data.BlasterImpactEffect;
import dev.pswg.item.BlasterEffectiveStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/**
 * Server impact operations.
 */
public final class BlasterImpacts
{
	/**
	 * Adds native permission checks to the vanilla explosion path.
	 */
	private static final class DamageCalculator extends ExplosionDamageCalculator
	{
		/**
		 * Captured impact and owner.
		 */
		private final BlasterEffectContext _context;

		/**
		 * Effect settings.
		 */
		private final BlasterExplosionEffect _effect;

		private DamageCalculator(BlasterEffectContext context, BlasterExplosionEffect effect)
		{
			_context = context;
			_effect = effect;
		}

		/**
		 * Restricts destruction to the authored tag and server permissions.
		 */
		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float power)
		{
			return _effect.destroyBlocks().isPresent()
			       && state.is(TagKey.create(Registries.BLOCK, _effect.destroyBlocks().orElseThrow()))
			       && state.getDestroySpeed(_context.world(), pos) >= 0
			       && canChangeBlock(_context, pos);
		}

		/**
		 * Keeps native damage and player/team immunity checks.
		 */
		@Override
		public boolean shouldDamageEntity(Explosion explosion, Entity target)
		{
			return _effect.damageEntities() && _context.canAffect(target);
		}

		/**
		 * Does not push protected players when damage is disabled by policy.
		 */
		@Override
		public float getKnockbackMultiplier(Entity target)
		{
			return _context.canAffect(target) ? _effect.knockback() : 0;
		}

		/**
		 * Uses captured weapon damage with native exposure and radial falloff.
		 */
		@Override
		public float getEntityDamageAmount(Explosion explosion, Entity target, float exposure)
		{
			var radial = Math.max(0, 1 - target.distanceToSqr(explosion.center()) / Math.pow(explosion.radius() * 2, 2));
			var shot = _context.shot();
			var falloff = new BlasterEffectiveStats(shot.stats(), 1, 1).damageMultiplierAt(_context.distance());
			return (float)(shot.stats().damage() * falloff * radial * exposure);
		}
	}

	/**
	 * Applies one contact effect without ordinary lethal damage.
	 */
	public static void contact(BlasterEffectContext context, BlasterImpactEffect effect)
	{
		if (effect.kind() == BlasterImpactEffect.ImpactKind.ION && context.hit() instanceof BlockHitResult block)
		{
			if (canChangeBlock(context, block.getBlockPos()) && context.world().getBlockEntity(block.getBlockPos()) instanceof IonSensitive powered)
			{
				powered.disrupt(context.world(), context.owner(), effect.durationTicks());
			}
			return;
		}

		if (!(context.hit() instanceof EntityHitResult hit) || !context.canAffect(hit.getEntity()))
		{
			return;
		}

		var target = hit.getEntity();
		if (effect.kind() == BlasterImpactEffect.ImpactKind.TRAINING)
		{
			var point = hit.getLocation();
			context.world().sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 3, 0.1, 0.1, 0.1, 0);
			return;
		}

		if (effect.kind() == BlasterImpactEffect.ImpactKind.ION)
		{
			ion(context, target, effect.durationTicks());
			return;
		}

		stun(context, target, effect.durationTicks());
	}

	/**
	 * Selects nearest visible cone targets. A target is affected at most once.
	 */
	public static void area(BlasterEffectContext context, BlasterAreaEffect effect)
	{
		var range = Math.min(effect.range(), context.shot().stats().range());
		var box = new AABB(context.origin(), context.origin()).inflate(range);
		var targets = context.world().getEntities(context.owner(), box, context::canAffect);
		targets.sort(Comparator.comparingDouble(target -> target.distanceToSqr(context.origin())));
		var remaining = effect.maximumTargets();

		for (var target : targets)
		{
			if (effect.kind() != BlasterAreaEffect.AreaKind.REPULSOR && effect.kind() != BlasterAreaEffect.AreaKind.ION && !(target instanceof LivingEntity))
				continue;

			var point = target.getBoundingBox().getCenter();
			var delta = point.subtract(context.origin());
			if (delta.lengthSqr() > range * range || !inCone(delta, context.direction(), effect.coneDegrees()))
				continue;

			if (!context.visible(point, effect.blockFluids()))
				continue;

			if (effect.kind() == BlasterAreaEffect.AreaKind.ION && !ionTarget(target))
				continue;

			applyAreaTarget(context, effect, target, delta);
			if (--remaining == 0)
				break;
		}
	}

	/**
	 * Tests a cone for each target.
	 */
	public static boolean inCone(Vec3 delta, Vec3 axis, float halfAngleDegrees)
	{
		return delta.lengthSqr() < 1.0E-12 || delta.normalize().dot(axis) >= Math.cos(Math.toRadians(halfAngleDegrees));
	}

	/**
	 * Applies the selected cone operation to one permitted target.
	 */
	private static void applyAreaTarget(BlasterEffectContext context, BlasterAreaEffect effect, Entity target, Vec3 delta)
	{
		if (effect.kind() != BlasterAreaEffect.AreaKind.SHOCK && target instanceof LivingEntity living && living.isBlocking())
		{
			return;
		}
		switch (effect.kind())
		{
			case STUN -> stun(context, target, effect.durationTicks());
			case ION -> ion(context, target, effect.durationTicks());
			case REPULSOR -> push(context, target, delta, effect.strength());
			case SHOCK ->
			{
				damage(context, target, (float)delta.length());
				stun(context, target, effect.durationTicks());
			}
		}
	}

	/**
	 * Routes lethal hits through the native damage pipeline.
	 */
	public static void damage(BlasterEffectContext context, Entity target, float distance)
	{
		if (!context.canAffect(target))
		{
			return;
		}

		var stats = context.shot().stats();
		var amount = stats.damage() * new BlasterEffectiveStats(stats, 1, 1).damageMultiplierAt(distance);
		if (amount > 0)
		{
			target.hurtServer(context.world(), context.damageSource(), amount);
		}
	}

	/**
	 * Applies native knockback resistance and publishes the motion change.
	 */
	private static void push(BlasterEffectContext context, Entity target, Vec3 delta, float strength)
	{
		var direction = delta.normalize();

		if (target instanceof LivingEntity living)
		{
			living.knockback(strength, -direction.x, -direction.z, context.damageSource(), 0, true);
		}
		else
		{
			target.push(direction.x * strength, direction.y * strength, direction.z * strength);
		}

		target.needsSync = true;
	}

	/**
	 * Adds a recoverable stun. A raised native shield blocks it.
	 */
	private static void stun(BlasterEffectContext context, Entity target, int duration)
	{
		if (target instanceof LivingEntity living && !living.isBlocking())
		{
			living.addEffect(new MobEffectInstance(BlasterStatusEffects.STUNNED, duration), context.owner());
		}
	}

	/**
	 * Accepts only tagged entities or a code-provided powered-object capability.
	 */
	private static boolean ionTarget(Entity target)
	{
		return target instanceof IonSensitive
		       || target instanceof LivingEntity && target.getType().builtInRegistryHolder().is(BlasterShot.ION_SUSCEPTIBLE);
	}

	/**
	 * Disrupts power without applying ordinary lethal damage.
	 */
	private static void ion(BlasterEffectContext context, Entity target, int duration)
	{
		if (!ionTarget(target))
			return;

		if (target instanceof IonSensitive powered)
			powered.disrupt(context.world(), context.owner(), duration);

		if (target instanceof LivingEntity living && !living.isBlocking())
			living.addEffect(new MobEffectInstance(BlasterStatusEffects.ION_DISRUPTION, duration), context.owner());
	}

	/**
	 * Uses native explosion exposure, damage, drops and player motion packets.
	 */
	public static void explode(BlasterEffectContext context, BlasterExplosionEffect effect)
	{
		var point = context.origin();

		if (effect.blockFluids() && !context.world().getFluidState(BlockPos.containing(point)).isEmpty())
			return;

		var interaction = effect.destroyBlocks().isPresent() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
		context.world().explode(
				context.direct(),
				context.world().damageSources().explosion(context.direct(), context.owner()),
				new DamageCalculator(context, effect),
				point.x,
				point.y,
				point.z,
				effect.radius(),
				false,
				interaction
		);
	}

	/**
	 * Requires mobGriefing, player build permission and per-position interaction permission.
	 */
	private static boolean canChangeBlock(BlasterEffectContext context, BlockPos pos)
	{
		if (context.owner() == null || !context.world().getGameRules().get(GameRules.MOB_GRIEFING) || !context.world().mayInteract(context.owner(), pos))
			return false;

		return context.owner() instanceof Player player && player.mayBuild() && player.mayInteract(context.world(), pos);
	}

	/**
	 * Breaks only a permitted, tagged impact block.
	 */
	public static void breakBlock(BlasterEffectContext context, Identifier blocks)
	{
		if (!(context.hit() instanceof BlockHitResult hit) || hit.isWorldBorderHit())
			return;

		var pos = hit.getBlockPos();
		var state = context.world().getBlockState(pos);
		if (state.is(TagKey.create(Registries.BLOCK, blocks)) && state.getDestroySpeed(context.world(), pos) >= 0 && canChangeBlock(context, pos))
			context.world().destroyBlock(pos, true, context.owner());
	}

	private BlasterImpacts()
	{
	}
}
