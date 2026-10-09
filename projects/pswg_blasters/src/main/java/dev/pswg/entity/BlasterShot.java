package dev.pswg.entity;

import dev.pswg.Blasters;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.data.BlasterBehaviorProfile;
import dev.pswg.data.BlasterImpactEffect;
import dev.pswg.data.BlasterStats;
import dev.pswg.generated.codecs.IBlasterShotCodec;
import dev.pswg.item.BlasterEffectiveStats;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Blaster shot data.
 */
@GenerateCodec(strict = true)
public record BlasterShot(
		@SelfCodec BlasterStats stats,
		@SelfCodec BlasterBehaviorProfile behavior
) implements IBlasterShotCodec
{
	/**
	 * Entities that are affected by ion shots.
	 */
	public static final TagKey<EntityType<?>> ION_SUSCEPTIBLE = TagKey.create(Registries.ENTITY_TYPE, Blasters.id("ion_susceptible"));

	/**
	 * Captures effective damage.
	 */
	public static BlasterShot capture(
			BlasterStats stats,
			BlasterBehaviorProfile behavior,
			BlasterStats.Trigger trigger,
			int heldChargeTicks,
			int loadedUnits
	)
	{
		if (behavior.chargedShot().isPresent())
		{
			var charged = behavior.chargedShot().orElseThrow();

			var fraction = switch (charged.source())
			{
				case HELD_DURATION -> Math.clamp((double)heldChargeTicks / ((BlasterStats.ChargeTrigger)trigger).maximumChargeTicks(), 0, 1);
				case LOADED_COMPONENT_CHARGE -> Math.clamp((double)loadedUnits / ((BlasterStats.ChargeStoreFeed)stats.ammo().feed()).chargeCapacityUnits(), 0, 1);
			};

			var multiplier = 1 + fraction * (charged.maximumDamageMultiplier() - 1.0);
			stats = stats.withDamage((float)Math.min((double)stats.damage() * multiplier, Float.MAX_VALUE));
		}

		return new BlasterShot(stats, behavior);
	}

	/**
	 * Resolves the first block/entity collision along a ray, including the world border.
	 */
	public static HitResult trace(ServerLevel world, Entity source, Vec3 from, Vec3 delta)
	{
		var block = world.clipIncludingBorder(new ClipContext(from, from.add(delta), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
		var end = block.getLocation();

		var entity = ProjectileUtil.getEntityHitResult(
				world,
				source,
				from,
				end,
				source.getBoundingBox().expandTowards(delta).inflate(1),
				target -> target.canBeHitByProjectile() && !target.isPassengerOfSameVehicle(source) && !(target instanceof BlasterBoltEntity),
				0
		);

		return entity == null ? block : entity;
	}

	/**
	 * Applies the hit effect.
	 */
	public void hit(ServerLevel world, Entity direct, Entity owner, HitResult result, float distance)
	{
		if (!(result instanceof EntityHitResult entityHit))
			return;

		var target = entityHit.getEntity();
		var damageType = world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
		                      .getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, behavior.damageType()));

		var source = new DamageSource(damageType, direct, owner);

		// Ignore invalid entities
		if (!target.isAlive() || target instanceof LivingEntity living && living.isInvulnerableTo(world, source))
			return;

		// Ignore disabled PvP
		if (owner instanceof Player shooter && target instanceof Player victim && !shooter.canHarmPlayer(victim))
			return;

		// Find the required effect
		var contact = behavior.effects().stream()
		                      .filter(effect -> effect.when() == BlasterBehaviorProfile.EffectPhase.ON_ENTITY_HIT)
		                      .filter(BlasterImpactEffect.class::isInstance)
		                      .map(BlasterImpactEffect.class::cast)
		                      .findFirst();

		// Apply the damage
		if (contact.isEmpty())
		{
			var damage = stats.damage() * new BlasterEffectiveStats(stats, 1, 1).damageMultiplierAt(distance);
			if (damage > 0)
				target.hurtServer(world, source, damage);

			return;
		}

		// Apply the effect
		var effect = contact.orElseThrow();
		if (effect.kind() == BlasterImpactEffect.ImpactKind.TRAINING)
		{
			// Training only emits crit particles
			world.sendParticles(ParticleTypes.CRIT, result.getLocation().x, result.getLocation().y, result.getLocation().z, 3, 0.1, 0.1, 0.1, 0);
			return;
		}

		// Ignore ion-resistant entities
		if (effect.kind() == BlasterImpactEffect.ImpactKind.ION && !target.getType().builtInRegistryHolder().is(ION_SUSCEPTIBLE))
			return;

		// Apply stun effect for stun and ion (temp)
		if (target instanceof LivingEntity living && !living.isBlocking())
		{
			living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, effect.durationTicks(), 4), owner);
			living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effect.durationTicks(), 4), owner);
		}
	}
}
