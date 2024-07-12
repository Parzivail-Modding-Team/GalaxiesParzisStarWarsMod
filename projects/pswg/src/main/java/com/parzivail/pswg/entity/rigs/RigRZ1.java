package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.RZ1Awing;
import com.parzivail.util.entity.TrackedAnimationValue;
import com.parzivail.util.generics.Make;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import org.joml.Matrix4f;

import java.util.HashSet;

public class RigRZ1 extends ModelRig<RZ1Awing>
{
	public static final RigRZ1 INSTANCE = new RigRZ1();
	public static final String CANNON_LEFT = "CannonLeft";
	public static final String CANNON_RIGHT = "CannonRight";
	public static final HashSet<String> CANNONS = Make.hashSet(CANNON_LEFT, CANNON_RIGHT);
	public static final String ENGINE_RIGHT = "EngineRight";
	public static final String ENGINE_LEFT = "EngineLeft";
	public static final String ENGINE_MIDDLE = "EngineMiddle";
	public static final HashSet<String> ENGINES = Make.hashSet(ENGINE_LEFT, ENGINE_RIGHT, ENGINE_MIDDLE);
	public static final String LEG_FRONT_TOP = "FrontLegTop";
	public static final String LEG_RIGHT_TOP = "RightLegTop";
	public static final String LEG_LEFT_TOP = "LeftLegTop";
	public static final HashSet<String> LEG_TOP = Make.hashSet(LEG_FRONT_TOP, LEG_RIGHT_TOP, LEG_LEFT_TOP);
	public static final String LEG_FRONT_MID = "FrontLegMid";
	public static final String LEG_RIGHT_MID = "RightLegMid";
	public static final String LEG_LEFT_MID = "LeftLegMid";
	public static final HashSet<String> LEG_MID = Make.hashSet(LEG_FRONT_MID, LEG_RIGHT_MID, LEG_LEFT_MID);
	public static final String LEG_FRONT_BOTTOM = "FrontLegBottom";
	public static final String LEG_RIGHT_BOTTOM = "RightLegBottom";
	public static final String LEG_LEFT_BOTTOM = "LeftLegBottom";
	public static final HashSet<String> LEG_BOTTOM = Make.hashSet(LEG_FRONT_BOTTOM, LEG_RIGHT_BOTTOM, LEG_LEFT_BOTTOM, LEG_FRONT_MID, LEG_RIGHT_MID, LEG_LEFT_MID, LEG_FRONT_TOP, LEG_RIGHT_TOP, LEG_LEFT_TOP);

	protected RigRZ1()
	{
		super(Resources.id("rigs/ship/awing_rz1.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(RZ1Awing target, String part, float tickDelta)
	{
		Matrix4f matrix4f = new Matrix4f();
		if (LEG_BOTTOM.contains(part))
		{
			var gearAnim = target.getGearAnim();
			var gearTimer = TrackedAnimationValue.getTimer(gearAnim, target.prevGearAnim, tickDelta);

			var timer = Ease.inOutCubic(Math.abs(gearTimer) / RZ1Awing.GEAR_ANIM_LENGTH);

			if (TrackedAnimationValue.isPositiveDirection(gearAnim))
				timer = 1 - timer;

			var legAngle = 1 * timer;

			switch (part)
			{
				case LEG_FRONT_MID, LEG_RIGHT_MID, LEG_LEFT_MID, LEG_FRONT_TOP, LEG_FRONT_BOTTOM, LEG_RIGHT_BOTTOM, LEG_LEFT_BOTTOM ->
						matrix4f.rotateX(MathUtil.toRadians(-legAngle));
				case LEG_LEFT_TOP, LEG_RIGHT_TOP -> matrix4f.rotateX(MathUtil.toRadians(legAngle));
				default -> throw new IndexOutOfBoundsException();
			}
		}

		return matrix4f;
	}
}
