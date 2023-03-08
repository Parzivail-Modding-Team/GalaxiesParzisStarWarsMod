package com.parzivail.util.entity.collision;

import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public interface ICollisionVolume
{
//	boolean collidesWith(ICollisionVolume other);
//
//	Vec3d closestDistanceTo(ICollisionVolume other);

	ICollisionVolume transform(Quaternionf q);

	ICollisionVolume transform(Matrix4f m);

	void resolveCapsuleCollision(CapsuleVolume sourceHitbox, MutableObject<Vec3d> m);
}
