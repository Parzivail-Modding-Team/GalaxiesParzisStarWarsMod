package com.parzivail.pswg.rig;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface IModelRig<T, P extends Enum<P>>
{
	void transform(MatrixStack stack, T target, P part);

	Vec3d getWorldPosition(MatrixStack stack, T target, P part, Vec3d localPosition);

	void transform(MatrixStack stack, T target, P part, float tickDelta);

	Vec3d getWorldPosition(MatrixStack stack, T target, P part, Vec3d localPosition, float tickDelta);
}
