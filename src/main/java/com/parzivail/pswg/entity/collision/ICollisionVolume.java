package com.parzivail.pswg.entity.collision;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public interface ICollisionVolume
{
//	boolean collidesWith(ICollisionVolume other);
//
//	Vec3d closestDistanceTo(ICollisionVolume other);

	ICollisionVolume transform(Quaternion q);

	ICollisionVolume transform(Matrix4f m);

	void resolveCapsuleCollision(CapsuleVolume sourceHitbox, MutableObject<Vec3d> m);
}
