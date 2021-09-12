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

	public Vec3d getWorldPosition(Transform stack, T target, Quaternion orientation, String socketName, float tickDelta)
	{
		stack.save();
		var entry = stack.value();
		var parent = entry.getModel();

		parent.multiply(orientation);

		var socket = RIG.sockets.get(socketName);
		for (var part : socket.ancestry)
		{
			stack.multiply(part.transform);
			stack.multiply(getPartTransformation(target, part.name, tickDelta));
		}

		stack.multiply(socket.transform);

		var vec = Matrix4fUtil.transform(Vec3d.ZERO, parent);
		stack.restore();

		return vec;
	}

	public abstract Matrix4f getPartTransformation(T target, String part, float tickDelta);
}
