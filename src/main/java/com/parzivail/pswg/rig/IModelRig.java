package com.parzivail.pswg.rig;

import com.parzivail.util.math.Transform;
import net.minecraft.util.math.Vec3d;

public interface IModelRig<T, P extends Enum<P>>
{
	void transform(Transform stack, T target, P part);

	Vec3d getWorldPosition(Transform stack, T target, P part, Vec3d localPosition);

	void transform(Transform stack, T target, P part, float tickDelta);

	Vec3d getWorldPosition(Transform stack, T target, P part, Vec3d localPosition, float tickDelta);
}
