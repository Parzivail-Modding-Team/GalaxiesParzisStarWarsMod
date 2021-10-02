package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public class ComplexCollisionManager
{
	public static Vec3d adjustMovementForCollisions(Entity entity, Vec3d currentMovement)
	{
		var box = entity.getBoundingBox();
		if (box.getAverageSideLength() < 1.0E-7D)
			return currentMovement;

		Box box2 = box.expand(4);
		var complexEntities = entity.world.getOtherEntities(entity, box2, other -> other instanceof IComplexEntityHitbox iceh && roughCollidesWith(iceh, entity)).stream();

		var sourceHitbox = CapsuleVolume.of(entity.getBoundingBox());

		final var m = new MutableObject<>(currentMovement);

		complexEntities.forEach(e -> {
			collide(entity, sourceHitbox, m, (IComplexEntityHitbox)e);
		});

		return m.getValue();
	}

	private static void collide(Entity entity, CapsuleVolume sourceHitbox, MutableObject<Vec3d> m, IComplexEntityHitbox e)
	{
		var hitbox = e.getCollision();

		for (var volume : hitbox)
		{
			var movement = m.getValue();
			var result = CollisionUtil.closestPointsOnSegments(sourceHitbox.start().add(movement), sourceHitbox.end().add(movement), volume.start(), volume.end());

			var intersectionRay = result.b().subtract(result.a());

			var minDistance = sourceHitbox.radius() + volume.radius();

			// Check if the volume is intersecting
			if (result.squareDistance() > minDistance * minDistance + 0.1f)
				continue;

			var intersectionLength = intersectionRay.length();
			var overlap = intersectionLength - minDistance;

			var rayDir = intersectionRay.normalize();

//			var aPos = result.a().add(rayDir.multiply(sourceHitbox.radius()));
//			var bPos = result.b().subtract(rayDir.multiply(volume.radius()));
//
//			entity.world.addParticle(ParticleTypes.FLAME, aPos.x, aPos.y, aPos.z, 0, 0, 0);
//			entity.world.addParticle(ParticleTypes.FLAME, bPos.x, bPos.y, bPos.z, 0, 0, 0);

			var impulse = rayDir.multiply(overlap);

			m.setValue(movement.add(impulse));
		}
	}

	private static boolean roughCollidesWith(IComplexEntityHitbox iceh, Entity other)
	{
		return true;
	}
}
