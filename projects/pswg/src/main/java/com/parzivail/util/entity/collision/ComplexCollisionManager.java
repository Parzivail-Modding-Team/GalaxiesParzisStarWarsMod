package com.parzivail.util.entity.collision;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;

public class ComplexCollisionManager
{
	public static Optional<Vec3d> adjustMovementForCollisions(Entity entity, Vec3d currentMovement)
	{
		var box = entity.getBoundingBox();
		if (box.getAverageSideLength() < 1.0E-7D)
			return Optional.empty();

		Box box2 = box.expand(5);
		var complexEntities = entity.getWorld().getOtherEntities(entity, box2, other -> other instanceof IComplexEntityHitbox iceh && roughCollidesWith(iceh, entity));

		var sourceHitbox = CapsuleVolume.of(entity.getBoundingBox());

		final var m = new MutableObject<>(currentMovement);

		var collided = false;
		for (var e : complexEntities)
			collided |= collide(entity, sourceHitbox, m, (IComplexEntityHitbox)e);

		if (collided)
			return Optional.of(m.getValue());
		return Optional.empty();
	}

	private static boolean collide(Entity entity, CapsuleVolume sourceHitbox, MutableObject<Vec3d> m, IComplexEntityHitbox e)
	{
		var hitbox = e.getCollision();

		var collided = false;
		for (var volume : hitbox)
			collided |= volume.resolveCapsuleCollision(sourceHitbox, m);

		return collided;
	}

	private static boolean roughCollidesWith(IComplexEntityHitbox iceh, Entity other)
	{
		return true;
	}
}
