package com.parzivail.pswg.rig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface IModelRig<T, P extends Enum<P>>
{
	void transform(MatrixStack stack, T target, P part);

	Vec3d getWorldPosition(MatrixStack stack, T target, P part, Vec3d localPosition);

	@Environment(EnvType.CLIENT)
	void transform(MatrixStack stack, T target, P part, float tickDelta);

	@Environment(EnvType.CLIENT)
	Vec3d getWorldPosition(MatrixStack stack, T target, P part, Vec3d localPosition, float tickDelta);
}
