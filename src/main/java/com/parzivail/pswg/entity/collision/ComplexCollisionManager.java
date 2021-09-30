package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ComplexCollisionManager
{
	public static void adjustMovementForCollisions(Entity entity, Vec3d proposedMovement, CallbackInfoReturnable<Vec3d> cir, Box box, Vec3d currentMovement)
	{
		if (box.getAverageSideLength() < 1.0E-7D)
			return;

		Box box2 = box.expand(4);
		var complexEntities = entity.world.getOtherEntities(entity, box2, other -> other instanceof IComplexEntityHitbox iceh && roughCollidesWith(iceh, entity)).stream();

		var sourceHitbox = CapsuleVolume.of(entity.getBoundingBox());

		final var m = new MutableObject<>(currentMovement);

		complexEntities.forEach(e -> {
			collide(entity, sourceHitbox, m, (IComplexEntityHitbox)e);
		});

		cir.setReturnValue(m.getValue());
	}

	private static void collide(Entity entity, CapsuleVolume sourceHitbox, MutableObject<Vec3d> m, IComplexEntityHitbox e)
	{
		var hitbox = e.getCollision();

		for (var volume : hitbox)
		{
			var result = CollisionUtil.closestPointsOnSegments(sourceHitbox.start().add(m.getValue()), sourceHitbox.end().add(m.getValue()), volume.start(), volume.end());

			var intersectionRay = result.b().subtract(result.a());
//			var aPos = result.a().add(rayDir.multiply(sourceHitbox.radius()));
//			var bPos = result.b().subtract(rayDir.multiply(volume.radius()));
//
//			entity.world.addParticle(ParticleTypes.FLAME, aPos.x, aPos.y, aPos.z, 0, 0, 0);
//			entity.world.addParticle(ParticleTypes.FLAME, bPos.x, bPos.y, bPos.z, 0, 0, 0);

			var minDistance = sourceHitbox.radius() + volume.radius();

			// Check if the volume is intersecting
			if (result.squareDistance() > minDistance * minDistance)
				continue;

			// Check if we're trying to move away from the intersection
			if (m.getValue().dotProduct(intersectionRay) <= 0)
				continue;

			var rayDir = intersectionRay.normalize();
			var intersectionLength = intersectionRay.length();
			var overlap = intersectionLength - minDistance;

			var in = m.getValue();
			var impulse = rayDir.multiply(intersectionRay.multiply(overlap / intersectionLength).dotProduct(rayDir));

			m.setValue(in.add(impulse));
		}
	}

	private static boolean roughCollidesWith(IComplexEntityHitbox iceh, Entity other)
	{
		return true;
	}
}
