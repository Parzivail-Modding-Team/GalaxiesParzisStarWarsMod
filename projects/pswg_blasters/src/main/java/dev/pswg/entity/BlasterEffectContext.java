package dev.pswg.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Server facts for one effect call. Datapacks cannot supply these.
 */
public record BlasterEffectContext(
		ServerLevel world,
		BlasterShot shot,
		Entity direct,
		Entity owner,
		HitResult hit,
		Vec3 origin,
		Vec3 direction,
		float distance
)
{
	/**
	 * Builds the captured direct-hit damage source.
	 */
	public DamageSource damageSource()
	{
		var type = world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
		                .getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, shot.behavior().damageType()));
		return new DamageSource(type, direct, owner);
	}

	/**
	 * Keeps immunity and player/team permissions for damage and nonlethal effects.
	 */
	public boolean canAffect(Entity target)
	{
		if (!target.isAlive() || target.isSpectator())
			return false;

		if (target instanceof LivingEntity living && living.isInvulnerableTo(world, damageSource()))
			return false;

		if (!(target instanceof LivingEntity) && target.isInvulnerable())
			return false;

		if (target instanceof Player && direct instanceof BlasterBoltEntity bolt && bolt.playerOwned() && !world.isPvpAllowed())
			return false;

		return !(owner instanceof Player shooter && target instanceof Player victim) || shooter.canHarmPlayer(victim);
	}

	/**
	 * Tests block-collider sight.
	 */
	public boolean visible(Vec3 target, boolean blockFluids)
	{
		var fluid = blockFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
		return world.clipIncludingBorder(new ClipContext(origin, target, ClipContext.Block.COLLIDER, fluid, direct)).getType() == HitResult.Type.MISS;
	}
}
