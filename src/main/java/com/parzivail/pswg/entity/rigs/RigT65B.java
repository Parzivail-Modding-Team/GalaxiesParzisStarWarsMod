package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.p3d.P3dModel;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.Transform;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RigT65B
{
	// TODO: rewrite entire class to properly utilize socket and parent/child rigging
	public static final RigT65B INSTANCE = new RigT65B();

	private static final P3dModel RIG = P3dModel.tryLoad(Resources.id("rigs/ship/xwing_t65b.p3dr"), false);

	private RigT65B()
	{
	}

	public Matrix4f getPartTransformation(T65BXwing target, String part, float tickDelta)
	{
		var wingAnim = target.getWingAnim();
		var wingTimer = TrackedAnimationValue.getTimer(wingAnim, target.prevWingAnim, tickDelta);

		var cockpitAnim = target.getCockpitAnim();
		var cockpitTimer = TrackedAnimationValue.getTimer(cockpitAnim, target.prevCockpitAnim, tickDelta);

		return getPartTransformation(part, TrackedAnimationValue.isPositiveDirection(wingAnim), wingTimer, TrackedAnimationValue.isPositiveDirection(cockpitAnim), cockpitTimer);
	}

	public Vec3d getWorldPosition(Transform stack, T65BXwing target, Quaternion orientation, String socketName, float tickDelta)
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

	private Matrix4f getPartTransformation(String part, boolean wingsOpening, float wingTimer, boolean cockpitOpening, float cockpitTimer)
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();

		if (part.startsWith("Wing"))
		{
			var timer = Ease.inCubic(Math.abs(wingTimer) / 20);

			if (wingsOpening)
				timer = 1 - timer;

			var wingAngle = 13 * timer;

			switch (part)
			{
				case "WingTopLeft", "WingBottomRight" -> matrix4f.multiply(new Quaternion(0, wingAngle, 0, true));
				case "WingBottomLeft", "WingTopRight" -> matrix4f.multiply(new Quaternion(0, -wingAngle, 0, true));
				default -> throw new IndexOutOfBoundsException();
			}
		}
		else if (part.equals("Cockpit"))
		{
			var timer = Ease.inCubic(Math.abs(cockpitTimer) / 20);

			if (cockpitOpening)
				timer = 1 - timer;

			var cockpitAngle = 50 * timer;

			matrix4f.multiply(new Quaternion(cockpitAngle, 0, 0, true));
		}

		return matrix4f;
	}
}
