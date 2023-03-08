package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.generics.Make;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import org.joml.Matrix4f;

import java.util.HashSet;

public class RigT65B extends ModelRig<T65BXwing>
{
	public static final RigT65B INSTANCE = new RigT65B();

	public static final String COCKPIT = "Cockpit";

	public static final String WING_TOP_LEFT = "WingTopLeft";
	public static final String WING_TOP_RIGHT = "WingTopRight";
	public static final String WING_BOTTOM_LEFT = "WingBottomLeft";
	public static final String WING_BOTTOM_RIGHT = "WingBottomRight";
	public static final HashSet<String> WINGS = Make.hashSet(WING_TOP_LEFT, WING_TOP_RIGHT, WING_BOTTOM_LEFT, WING_BOTTOM_RIGHT);

	public static final String CANNON_TOP_LEFT = "CannonTopLeft";
	public static final String CANNON_TOP_RIGHT = "CannonTopRight";
	public static final String CANNON_BOTTOM_LEFT = "CannonBottomLeft";
	public static final String CANNON_BOTTOM_RIGHT = "CannonBottomRight";
	public static final HashSet<String> CANNONS = Make.hashSet(CANNON_TOP_LEFT, CANNON_TOP_RIGHT, CANNON_BOTTOM_LEFT, CANNON_BOTTOM_RIGHT);

	public static final String ENGINE_TOP_LEFT = "EngineTopLeft";
	public static final String ENGINE_TOP_RIGHT = "EngineTopRight";
	public static final String ENGINE_BOTTOM_LEFT = "EngineBottomLeft";
	public static final String ENGINE_BOTTOM_RIGHT = "EngineBottomRight";
	public static final HashSet<String> ENGINES = Make.hashSet(ENGINE_TOP_LEFT, ENGINE_TOP_RIGHT, ENGINE_BOTTOM_LEFT, ENGINE_BOTTOM_RIGHT);

	private RigT65B()
	{
		super(Resources.id("rigs/ship/xwing_t65b.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(T65BXwing target, String part, float tickDelta)
	{
		Matrix4f matrix4f = new Matrix4f();

		if (WINGS.contains(part))
		{
			var wingAnim = target.getWingAnim();
			var wingTimer = TrackedAnimationValue.getTimer(wingAnim, target.prevWingAnim, tickDelta);

			var timer = Ease.inOutCubic(Math.abs(wingTimer) / T65BXwing.WING_ANIM_LENGTH);

			if (TrackedAnimationValue.isPositiveDirection(wingAnim))
				timer = 1 - timer;

			var wingAngle = 13 * timer;

			switch (part)
			{
				case WING_TOP_LEFT, WING_BOTTOM_RIGHT -> matrix4f.rotateZ(MathUtil.toRadians(-wingAngle));
				case WING_BOTTOM_LEFT, WING_TOP_RIGHT -> matrix4f.rotateZ(MathUtil.toRadians(wingAngle));
				default -> throw new IndexOutOfBoundsException();
			}
		}
		else if (part.equals(COCKPIT))
		{
			var cockpitAnim = target.getCockpitAnim();
			var cockpitTimer = TrackedAnimationValue.getTimer(cockpitAnim, target.prevCockpitAnim, tickDelta);

			var timer = Ease.inOutCubic(Math.abs(cockpitTimer) / T65BXwing.COCKPIT_ANIM_LENGTH);

			if (TrackedAnimationValue.isPositiveDirection(cockpitAnim))
				timer = 1 - timer;

			var cockpitAngle = 50 * timer;

			matrix4f.rotateX(MathUtil.toRadians(cockpitAngle));
		}

		return matrix4f;
	}
}
