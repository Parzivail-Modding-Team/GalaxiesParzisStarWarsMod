package com.parzivail.pswg.entity.collision;

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

		Box box2 = box.expand(5);
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
			volume.resolveCapsuleCollision(sourceHitbox, m);
	}

	private static boolean roughCollidesWith(IComplexEntityHitbox iceh, Entity other)
	{
		return true;
	}
}
