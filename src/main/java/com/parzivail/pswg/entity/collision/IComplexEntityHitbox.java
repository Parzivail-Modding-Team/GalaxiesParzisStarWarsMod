package com.parzivail.pswg.entity.collision;

import net.minecraft.util.math.Quaternion;

public interface IComplexEntityHitbox
{
	CapsuleVolume[] getCollision();

	Quaternion getRotation();
}
