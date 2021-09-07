package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.access.util.Matrix4fAccessUtil;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.math.Transform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class RigT65B
{
	// TODO: rewrite entire class to properly utilize socket and parent/child rigging
	public static final RigT65B INSTANCE = new RigT65B();

//	private static final PR3RFile RIG = PR3RFile.tryLoad(Resources.id("rigs/ship/xwing_t65b.pr3r"));

	private RigT65B()
	{
	}

	public void transform(Transform stack, T65BXwing target, String part)
	{
		var entry = stack.value();
		var modelMat = entry.getModel();

		var objectRotation = getRotation(target, part);

		modelMat.multiply(objectRotation);
	}

	private Quaternion getRotation(T65BXwing entity, String part)
	{
		var wingAnim = entity.getWingAnim();
		var cockpitAnim = entity.getCockpitAnim();

		return getRotation(part, wingAnim.isPositiveDirection(), wingAnim.getTimer(), cockpitAnim.isPositiveDirection(), cockpitAnim.getTimer());
	}

	public Vec3d getWorldPosition(Transform stack, T65BXwing target, String part, Vec3d localPosition)
	{
		stack.save();

		stack.multiply(target.getRotation());

		var entry = stack.value();
		var parent = entry.getModel();

		// TODO:
//		var rig = RIG.objects().get(part);
//		parent.multiply(rig);

		transform(stack, target, part);

		var vec = Matrix4fAccessUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	@Environment(EnvType.CLIENT)
	public void transform(Transform stack, T65BXwing target, String part, float tickDelta)
	{
		var wingAnim = target.getWingAnim();
		var wingTimer = wingAnim.getTimer();

		if (wingTimer > 0)
			wingTimer -= tickDelta;

		var cockpitAnim = target.getCockpitAnim();
		var cockpitTimer = cockpitAnim.getTimer();

		if (cockpitTimer > 0)
			cockpitTimer -= tickDelta;

		stack.multiply(getRotation(part, wingAnim.isPositiveDirection(), wingTimer, cockpitAnim.isPositiveDirection(), cockpitTimer));
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getWorldPosition(Transform stack, T65BXwing target, String part, Vec3d localPosition, float tickDelta)
	{
		stack.save();
		var entry = stack.value();
		var parent = entry.getModel();

		// TODO:
//		var rig = RIG.objects().get(part);
//		parent.multiply(rig);

		transform(stack, target, part, tickDelta);

		parent.multiply(target.getRotation());

		var vec = Matrix4fAccessUtil.transform(localPosition, parent);
		stack.restore();

		return vec;
	}

	private Quaternion getRotation(String part, boolean wingsOpening, float wingTimer, boolean cockpitOpening, float cockpitTimer)
	{
		if (part.startsWith("Wing"))
		{
			var timer = Math.abs(wingTimer);

			float wingAngle;

			if (wingsOpening)
				wingAngle = 13 * (20 - timer) / 20;
			else
				wingAngle = 13 * timer / 20;

			return switch (part)
					{
						case "WingTopLeft", "WingBottomRight" -> new Quaternion(0, wingAngle, 0, true);
						case "WingBottomLeft", "WingTopRight" -> new Quaternion(0, -wingAngle, 0, true);
						default -> throw new IndexOutOfBoundsException();
					};
		}
		else if (part.equals("Cockpit"))
		{
			var timer = Math.abs(cockpitTimer);

			float cockpitAngle;

			if (cockpitOpening)
				cockpitAngle = 50 * (20 - timer) / 20;
			else
				cockpitAngle = 50 * timer / 20;

			return new Quaternion(cockpitAngle, 0, 0, true);
		}

		return new Quaternion(Quaternion.IDENTITY);
	}
}
