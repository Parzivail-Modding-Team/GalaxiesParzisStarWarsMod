package com.parzivail.util.entity.collision;

import org.joml.Quaternionf;

public interface IComplexEntityHitbox
{
	ICollisionVolume[] getCollision();

	Quaternionf getRotation();
}
