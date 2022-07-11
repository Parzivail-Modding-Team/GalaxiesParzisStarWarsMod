package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public record CapsuleVolume(Vec3d start, Vec3d end, double radius) implements ICollisionVolume
{
	public static CapsuleVolume of(Box box)
	{
		var halfHeight = box.getYLength() / 2;
		var r = Math.min(box.getZLength() / 2, halfHeight);
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
	public ICollisionVolume transform(Quaternion q)
	{
		return new CapsuleVolume(QuatUtil.rotate(start, q), QuatUtil.rotate(end, q), radius);
	}

	@Override
	public ICollisionVolume transform(Matrix4f m)
	{
		return new CapsuleVolume(Matrix4fUtil.transform(start, m), Matrix4fUtil.transform(end, m), radius);
	}

	@Override
	public void resolveCapsuleCollision(CapsuleVolume sourceHitbox, MutableObject<Vec3d> movementContainer)
	{
		var movement = movementContainer.getValue();
		var result = CollisionUtil.closestPointsOnSegments(sourceHitbox.start().add(movement), sourceHitbox.end().add(movement), start, end);

		var intersectionRay = result.b().subtract(result.a());

		var minDistance = sourceHitbox.radius() + radius;

		// Check if the volume is intersecting
		if (result.squareDistance() > minDistance * minDistance)
			return;

		var intersectionLength = intersectionRay.length();
		var overlap = intersectionLength - minDistance;

		var rayDir = intersectionRay.normalize();

		//			var aPos = result.a().add(rayDir.multiply(sourceHitbox.radius()));
		//			var bPos = result.b().subtract(rayDir.multiply(volume.radius()));
		//
		//			entity.world.addParticle(ParticleTypes.FLAME, aPos.x, aPos.y, aPos.z, 0, 0, 0);
		//			entity.world.addParticle(ParticleTypes.FLAME, bPos.x, bPos.y, bPos.z, 0, 0, 0);

		var impulse = rayDir.multiply(overlap);

		movementContainer.setValue(movement.add(impulse));
	}
}
