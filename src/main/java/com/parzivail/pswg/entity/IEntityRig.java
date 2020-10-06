package com.parzivail.pswg.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public interface IEntityRig<T extends Entity, P extends Enum<P>>
{
	Quaternion getRotation(T entity, P part);

	Vec3d getWorldPosition(T entity, P part, Vec3d localPosition);

	@Environment(EnvType.CLIENT)
	Quaternion getRotation(T entity, P part, float tickDelta);

	@Environment(EnvType.CLIENT)
	Vec3d getWorldPosition(T entity, P part, Vec3d localPosition, float tickDelta);
}
