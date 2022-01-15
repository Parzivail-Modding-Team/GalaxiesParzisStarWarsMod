package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.math.Ease;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class RigT65B extends ModelRig<T65BXwing>
{
	public static final RigT65B INSTANCE = new RigT65B();

	private RigT65B()
	{
		super(Resources.id("rigs/ship/xwing_t65b.p3dr"));
	}

	public Matrix4f getPartTransformation(T65BXwing target, String part, float tickDelta)
	{
		var wingAnim = target.getWingAnim();
		var wingTimer = TrackedAnimationValue.getTimer(wingAnim, target.prevWingAnim, tickDelta);

		var cockpitAnim = target.getCockpitAnim();
		var cockpitTimer = TrackedAnimationValue.getTimer(cockpitAnim, target.prevCockpitAnim, tickDelta);

		return getPartTransformation(part, TrackedAnimationValue.isPositiveDirection(wingAnim), wingTimer, TrackedAnimationValue.isPositiveDirection(cockpitAnim), cockpitTimer);
	}

	private Matrix4f getPartTransformation(String part, boolean wingsOpening, float wingTimer, boolean cockpitOpening, float cockpitTimer)
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();

		if (part.startsWith("Wing"))
		{
			var timer = Ease.inOutCubic(Math.abs(wingTimer) / T65BXwing.WING_ANIM_LENGTH);

			if (wingsOpening)
				timer = 1 - timer;

			var wingAngle = 13 * timer;

			switch (part)
			{
				case "WingTopLeft", "WingBottomRight" -> matrix4f.multiply(new Quaternion(0, 0, -wingAngle, true));
				case "WingBottomLeft", "WingTopRight" -> matrix4f.multiply(new Quaternion(0, 0, wingAngle, true));
				default -> throw new IndexOutOfBoundsException();
			}
		}
		else if (part.equals("Cockpit"))
		{
			var timer = Ease.inOutCubic(Math.abs(cockpitTimer) / T65BXwing.COCKPIT_ANIM_LENGTH);

			if (cockpitOpening)
				timer = 1 - timer;

			var cockpitAngle = 50 * timer;

			matrix4f.multiply(new Quaternion(cockpitAngle, 0, 0, true));
		}

		return matrix4f;
	}
}
