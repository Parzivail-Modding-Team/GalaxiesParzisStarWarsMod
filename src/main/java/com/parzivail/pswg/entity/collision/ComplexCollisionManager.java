package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.MathUtil;
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
			var iceh = (IComplexEntityHitbox)e;
			var hitbox = iceh.getCollision();

			for (var volume : hitbox)
			{
				var intersectionRay = sourceHitbox.closestDistanceTo(volume);

				// Check if the volume is intersecting
				if (intersectionRay.lengthSquared() > volume.radius() * volume.radius() + sourceHitbox.radius() * sourceHitbox.radius())
					continue;

				// Check if we're trying to move away from the intersection
				if (currentMovement.dotProduct(intersectionRay) <= 0)
					continue;

				var newMovement = currentMovement.subtract(MathUtil.project(currentMovement, intersectionRay));

				if (newMovement.lengthSquared() < m.getValue().lengthSquared())
					m.setValue(newMovement);
			}
		});

		cir.setReturnValue(m.getValue());
	}

	private static boolean roughCollidesWith(IComplexEntityHitbox iceh, Entity other)
	{
		return true;
	}
}
