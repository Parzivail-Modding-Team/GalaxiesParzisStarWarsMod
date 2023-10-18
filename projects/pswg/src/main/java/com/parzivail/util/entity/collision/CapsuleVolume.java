package com.parzivail.util.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public record CapsuleVolume(Vec3d start, Vec3d end, double radius) implements ICollisionVolume
{
	public static CapsuleVolume of(Box box)
	{
		var halfHeight = box.getLengthY() / 2;
		var r = Math.min(box.getLengthZ() / 2, halfHeight);
		return new CapsuleVolume(box.getCenter().subtract(0, halfHeight - r, 0),
		                         box.getCenter().add(0, halfHeight - r, 0),
		                         r);
	}

	//	public boolean collidesWith(ICollisionVolume other)
	//	{
	//		if (other instanceof CapsuleVolume capsule)
	//		{
	//			var result = CollisionUtil.closestPointsOnSegments(start, end, capsule.start, capsule.end);
	//			var r = radius + capsule.radius;
	//			return result.squareDistance() <= r * r;
	//		}
	//
	//		return false;
	//	}
	//
	//	public Vec3d closestDistanceTo(ICollisionVolume other)
	//	{
	//		if (other instanceof CapsuleVolume capsule)
	//		{
	//			var result = CollisionUtil.closestPointsOnSegments(start, end, capsule.start, capsule.end);
	//			return result.b().subtract(result.a());
	//		}
	//
	//		return null;
	//	}

	@Override
	public ICollisionVolume transform(Quaternionf q)
	{
		return new CapsuleVolume(QuatUtil.rotate(start, q), QuatUtil.rotate(end, q), radius);
	}

	@Override
	public ICollisionVolume transform(Matrix4f m)
	{
		return new CapsuleVolume(MathUtil.transform(start, m), MathUtil.transform(end, m), radius);
	}

	@Override
	public boolean resolveCapsuleCollision(CapsuleVolume sourceHitbox, MutableObject<Vec3d> movementContainer)
	{
		var movement = movementContainer.getValue();
		var result = CollisionUtil.closestPointsOnSegments(sourceHitbox.start().add(movement), sourceHitbox.end().add(movement), start, end);

		var intersectionRay = result.b().subtract(result.a());

		var minDistance = sourceHitbox.radius() + radius;

		// Check if the volume is intersecting
		if (result.squareDistance() > minDistance * minDistance)
			return false;

		var intersectionLength = intersectionRay.length();
		var overlap = intersectionLength - minDistance;

		var rayDir = intersectionRay.normalize();

		var impulse = rayDir.multiply(overlap);

		movementContainer.setValue(movement.add(impulse));
		return true;
	}
}
