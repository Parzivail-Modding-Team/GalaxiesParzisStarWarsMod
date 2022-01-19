package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.client.render.p3d.P3dModel;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.Transform;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public abstract class ModelRig<T>
{
	private final P3dModel RIG;

	protected ModelRig(Identifier path)
	{
		RIG = P3dModel.tryLoad(path, false);
	}

	public Matrix4f getTransform(T target, Quaternion orientation, String socketName, float tickDelta)
	{
		var mat = new Matrix4f();
		mat.loadIdentity();

		mat.multiply(orientation);

		var socket = RIG.transformables.get(socketName);
		for (var part : socket.ancestry)
		{
			mat.multiply(part.transform);
			mat.multiply(getPartTransformation(target, part.name, tickDelta));
		}

		mat.multiply(socket.transform);

		return mat;
	}

	public Vec3d getWorldPosition(Transform stack, T target, Quaternion orientation, String socketName, float tickDelta)
	{
		stack.save();
		stack.multiply(getTransform(target, orientation, socketName, tickDelta));

		var entry = stack.value();
		var parent = entry.getModel();
		var vec = Matrix4fUtil.transform(Vec3d.ZERO, parent);
		stack.restore();

		return vec;
	}

	public abstract Matrix4f getPartTransformation(T target, String part, float tickDelta);
}
