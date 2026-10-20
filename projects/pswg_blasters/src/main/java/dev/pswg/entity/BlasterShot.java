package dev.pswg.entity;

import dev.pswg.Blasters;
import dev.pswg.codecgenerator.GenerateCodec;
import dev.pswg.codecgenerator.SelfCodec;
import dev.pswg.data.BlasterBehaviorProfile;
import dev.pswg.data.BlasterExplosionEffect;
import dev.pswg.data.BlasterImpactEffect;
import dev.pswg.data.BlasterStats;
import dev.pswg.generated.codecs.IBlasterShotCodec;
import dev.pswg.math.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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
	 * Captures pellet damage and clears scatter.
	 */
	public BlasterShot pellet()
	{
		if (behavior.scatter().isEmpty())
		{
			return this;
		}

		var profile = new BlasterBehaviorProfile(
				behavior.delivery(),
				behavior.damageType(),
				behavior.effects(),
				behavior.muzzles(),
				behavior.chargedShot(),
				Optional.empty(),
				behavior.flight(),
				behavior.allowAds(),
				behavior.activateBlocks()
		);

		return new BlasterShot(stats.withDamage(stats.damage() * behavior.scatter().orElseThrow().damageMultiplier()), profile);
	}

	/**
	 * Emits a committed shot from one captured origin and aim. Each pellet resolves its own first collision.
	 */
	public void emit(ServerLevel world, LivingEntity user, boolean aiming)
	{
		emit(world, user, aiming, 0, false);
	}

	/**
	 * Emits selected muzzles from the captured view. The caller commits costs first.
	 */
	public void emit(ServerLevel world, LivingEntity user, boolean aiming, int muzzleCursor, boolean mirrored)
	{
		var spread = stats.spread();
		double angle = aiming ? spread.aimDegrees() : spread.hipDegrees();
		if (user.getDeltaMovement().horizontalDistanceSqr() > 0.0001)
		{
			angle *= spread.movingMultiplier();
		}

		if (user.isSprinting())
		{
			angle *= spread.sprintingMultiplier();
		}

		var cone = (float)Math.min(angle + behavior.scatter().map(BlasterBehaviorProfile.Scatter::coneDegrees).orElse(0f), 90);
		var count = behavior.scatter().map(BlasterBehaviorProfile.Scatter::pelletCount).orElse(1);
		var eye = user.getEyePosition();
		var aim = user.getViewVector(1).normalize();
		var pellet = pellet();
		var muzzleCount = behavior.muzzles().map(BlasterBehaviorProfile.Muzzles::emissionCount).orElse(1);

		for (var muzzle = 0; muzzle < muzzleCount; muzzle++)
		{
			var origin = muzzleOrigin(world, user, eye, aim, muzzleCursor, muzzle, mirrored);
			var result = BlockHitResult.miss(origin, Direction.UP, BlockPos.containing(origin));
			applyEffects(
					BlasterBehaviorProfile.EffectPhase.ON_FIRE,
					new BlasterEffectContext(world, this, user, user, result, origin, aim, 0)
			);

			if (behavior.delivery() == BlasterBehaviorProfile.Delivery.PULSE)
			{
				continue;
			}

			for (var index = 0; index < count; index++)
			{
				var direction = RandomHelper.directionInCone(world.getRandom(), aim, cone);
				pellet.emitPellet(world, user, origin, direction);
			}
		}
	}

	/**
	 * Clips the view-local muzzle offset so an emitter cannot start behind a wall.
	 */
	private Vec3 muzzleOrigin(
			ServerLevel world,
			LivingEntity user,
			Vec3 eye,
			Vec3 aim,
			int cursor,
			int emission,
			boolean mirrored
	)
	{
		if (behavior.muzzles().isEmpty())
		{
			return eye;
		}

		var muzzles = behavior.muzzles().orElseThrow();
		var index = muzzles.socketIndex(cursor, emission);
		var offset = muzzles.offsets().get(muzzles.sockets().get(index));

		if (offset == null)
		{
			return eye;
		}

		var right = new Vec3(-1, 0, 0).yRot((float)-Math.toRadians(user.getYRot()));
		var up = right.cross(aim).normalize();
		var point = eye.add(right.scale(mirrored ? -offset.x() : offset.x()))
		               .add(up.scale(offset.y()))
		               .add(aim.scale(offset.z()));

		var clip = world.clipIncludingBorder(new ClipContext(eye, point, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
		return clip.getType() == HitResult.Type.MISS
		       ? point
		       : clip.getLocation().add(clip.getDirection().getUnitVec3().scale(0.01));
	}

	/**
	 * Returns the atomic resource/heat cost count. Scatter does not multiply it.
	 */
	public int costCount()
	{
		return behavior.muzzles().map(BlasterBehaviorProfile.Muzzles::costCount).orElse(1);
	}

	/**
	 * Uses the profile's native delivery route for one pellet.
	 */
	private void emitPellet(ServerLevel world, LivingEntity user, Vec3 origin, Vec3 direction)
	{
		if (behavior.delivery() == BlasterBehaviorProfile.Delivery.HITSCAN)
		{
			var result = trace(world, user, origin, direction.scale(stats.range()));
			hit(world, user, user, result, (float)origin.distanceTo(result.getLocation()));
			return;
		}

		var projectile = new BlasterBoltEntity(Blasters.BLASTER_BOLT_ENTITY, world);
		projectile.setOwner(user);
		projectile.setShot(this);
		projectile.setPos(origin);
		projectile.setDeltaMovement(direction.scale(behavior.flight().orElse(BlasterBehaviorProfile.Flight.ENERGY).speed()));
		ProjectileUtil.rotateTowardsMovement(projectile, 1);
		world.addFreshEntity(projectile);
	}

	/**
	 * Applies the hit effect.
	 */
	public void hit(ServerLevel world, Entity direct, Entity owner, HitResult result, float distance)
	{
		var point = result.getLocation();
		var direction = direct.getDeltaMovement().normalize();
		var context = new BlasterEffectContext(world, this, direct, owner, result, point, direction, distance);

		if (result instanceof EntityHitResult entityHit)
		{
			applyEntityHit(context, entityHit);
		}
		else if (result.getType() == HitResult.Type.BLOCK && result instanceof BlockHitResult blockHit)
		{
			applyBlockHit(context, blockHit);
		}

		detonate(world, direct, owner, point, distance);
	}

	/**
	 * Applies direct damage unless a contact or explosion profile replaces it.
	 */
	private void applyEntityHit(BlasterEffectContext context, EntityHitResult hit)
	{
		var replacesDamage = behavior.effects().stream().anyMatch(effect ->
				                                                          effect instanceof BlasterExplosionEffect
				                                                          || effect instanceof BlasterImpactEffect && effect.when() == BlasterBehaviorProfile.EffectPhase.ON_ENTITY_HIT
		);

		if (!replacesDamage)
		{
			BlasterImpacts.damage(context, hit.getEntity(), context.distance());
		}

		applyEffects(BlasterBehaviorProfile.EffectPhase.ON_ENTITY_HIT, context);
	}

	/**
	 * Runs permitted native callbacks before block-hit effects.
	 */
	private void applyBlockHit(BlasterEffectContext context, BlockHitResult hit)
	{
		var world = context.world();
		var owner = context.owner();

		if (behavior.activateBlocks() && !hit.isWorldBorderHit() && owner != null && world.mayInteract(owner, hit.getBlockPos()))
		{
			var projectile = context.direct() instanceof BlasterBoltEntity bolt
			                 ? bolt : new BlasterBoltEntity(Blasters.BLASTER_BOLT_ENTITY, world);
			projectile.setOwner(owner);
			var state = world.getBlockState(hit.getBlockPos());
			state.onProjectileHit(world, state, hit, projectile);
		}

		applyEffects(BlasterBehaviorProfile.EffectPhase.ON_BLOCK_HIT, context);
	}

	/**
	 * Executes detonation effects once. Projectiles discard immediately after this call.
	 */
	public void detonate(ServerLevel world, Entity direct, Entity owner, Vec3 point, float distance)
	{
		var result = BlockHitResult.miss(point, Direction.UP, BlockPos.containing(point));
		var context = new BlasterEffectContext(world, this, direct, owner, result, point, direct.getDeltaMovement().normalize(), distance);
		applyEffects(BlasterBehaviorProfile.EffectPhase.ON_DETONATE, context);
	}

	/**
	 * Runs every matching effect in authored order.
	 */
	private void applyEffects(BlasterBehaviorProfile.EffectPhase phase, BlasterEffectContext context)
	{
		for (var effect : behavior.effects())
		{
			if (effect.when() == phase)
			{
				effect.apply(context);
			}
		}
	}
}
