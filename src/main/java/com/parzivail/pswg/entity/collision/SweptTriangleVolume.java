package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public record SweptTriangleVolume(Vec3d a, Vec3d b, Vec3d c, double radius) implements ICollisionVolume
{
	public ICollisionVolume transform(Quaternion q)
	{
		return new SweptTriangleVolume(QuatUtil.rotate(a, q), QuatUtil.rotate(b, q), QuatUtil.rotate(c, q), radius);
	}

	public ICollisionVolume transform(Matrix4f m)
	{
		return new SweptTriangleVolume(Matrix4fUtil.transform(a, m), Matrix4fUtil.transform(b, m), Matrix4fUtil.transform(c, m), radius);
	}

	@Override
	public void resolveCapsuleCollision(CapsuleVolume sourceHitbox, MutableObject<Vec3d> movementContainer)
	{
		var movement = movementContainer.getValue();
		var result = CollisionUtil.closestPointsTriangleSegment(sourceHitbox.start().add(movement), sourceHitbox.end().add(movement), a, b, c);

		var intersectionRay = result.b().subtract(result.a());

		var minDistance = sourceHitbox.radius() + radius;

		// Check if the volume is intersecting
		if (result.squareDistance() > minDistance * minDistance)
			return;

		var intersectionLength = intersectionRay.length();
		var overlap = intersectionLength - minDistance;

//		var aPos = result.a().add(rayDir.multiply(sourceHitbox.radius()));
//		var bPos = result.b().subtract(rayDir.multiply(radius));
//
//		MinecraftClient.getInstance().world.addParticle(ParticleTypes.FLAME, aPos.x, aPos.y, aPos.z, 0, 0, 0.1f);
//		MinecraftClient.getInstance().world.addParticle(ParticleTypes.FLAME, bPos.x, bPos.y, bPos.z, 0, 0, 0.1f);

		var impulse = intersectionRay.multiply(overlap / intersectionLength);

		var slideMovement = movement.add(impulse);

		movementContainer.setValue(slideMovement);
	}
}
