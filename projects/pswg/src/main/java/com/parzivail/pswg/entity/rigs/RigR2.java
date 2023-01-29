package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class RigR2 extends ModelRig<AstromechEntity>
{
	public static final RigR2 INSTANCE = new RigR2();

	private RigR2()
	{
		super(Resources.id("rigs/droid/r2.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(AstromechEntity target, String part, float tickDelta)
	{
		var m = new Matrix4f();

		var t = target.getLegDeltaExtension(tickDelta);

		var fullyExtendedLeg = 0.4535f;
		var legVisibleExt = 0.05f;

		var bodyYaw = MathHelper.lerp(tickDelta, target.prevYaw, target.getYaw());
		var headYaw = MathHelper.lerpAngleDegrees(tickDelta, target.prevHeadYaw, target.headYaw) - bodyYaw;
		var headPitch = -MathHelper.lerpAngleDegrees(tickDelta, target.prevPitch, target.getPitch()) * 0.2f * MathHelper.cos((float)(headYaw / 180 * Math.PI));

		var pushAngle = Ease.inCubic(t) * 15f;
		var bodyAngle = MathHelper.lerp(Ease.outCubic(t), headPitch, (23f + pushAngle));
		var centerLegExtension = Ease.outCubic(t) * fullyExtendedLeg;
		var legExtT = MathHelper.clamp((centerLegExtension - legVisibleExt) / (fullyExtendedLeg - legVisibleExt), 0, 1);
		var centerFootAngle = Ease.inCubic(legExtT) * (pushAngle - bodyAngle);

		switch (part)
		{
			case "left_leg" -> m.rotateX(MathUtil.toRadians(-pushAngle));
			case "right_foot" -> m.rotateX(MathUtil.toRadians(pushAngle));
			case "right_shoulder" -> m.rotateX(MathUtil.toRadians(-bodyAngle));
			case "center_leg" -> m.translate(0, -centerLegExtension, 0);
			case "center_foot" -> m.rotateX(MathUtil.toRadians(centerFootAngle));
			case "body" -> m.rotateX(MathUtil.toRadians(bodyAngle));
			case "head" -> m.rotateY(MathUtil.toRadians(-headYaw));
		}

		return m;
	}
}
