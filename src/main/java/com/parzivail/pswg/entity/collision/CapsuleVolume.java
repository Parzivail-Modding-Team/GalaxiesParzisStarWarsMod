package com.parzivail.pswg.entity.collision;

import com.parzivail.util.math.CollisionUtil;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public record CapsuleVolume(Vec3d start, Vec3d end, double radius)
{
	public static CapsuleVolume of(Box box)
	{
		var halfHeight = box.getYLength() / 2;
		var r = Math.min(box.getZLength() / 2, halfHeight);
		return new CapsuleVolume(box.getCenter().subtract(0, halfHeight - r, 0),
		                         box.getCenter().add(0, halfHeight - r, 0),
		                         r);
	}

	public boolean collidesWith(CapsuleVolume other)
	{
		var result = CollisionUtil.closestPointsOnSegments(start, end, other.start, other.end);
		var r = radius + other.radius;
		return result.squareDistance() <= r * r;
	}

	public Vec3d closestDistanceTo(CapsuleVolume other)
	{
		var result = CollisionUtil.closestPointsOnSegments(start, end, other.start, other.end);
		var chord = result.b().subtract(result.a());
		return chord;
	}

	public CapsuleVolume transform(Quaternion q)
	{
		return new CapsuleVolume(QuatUtil.rotate(start, q), QuatUtil.rotate(end, q), radius);
	}

	public CapsuleVolume transform(Matrix4f m)
	{
		return new CapsuleVolume(Matrix4fUtil.transform(start, m), Matrix4fUtil.transform(end, m), radius);
	}
}
