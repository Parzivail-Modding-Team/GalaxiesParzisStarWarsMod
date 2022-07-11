package com.parzivail.pswg.entity.collision;

import net.minecraft.util.math.Quaternion;

public interface IComplexEntityHitbox
{
	ICollisionVolume[] getCollision();

	Quaternion getRotation();
}
