package com.parzivail.pswg.entity.collision;

import org.joml.Quaternionf;

public interface IComplexEntityHitbox
{
	ICollisionVolume[] getCollision();

	Quaternionf getRotation();
}
