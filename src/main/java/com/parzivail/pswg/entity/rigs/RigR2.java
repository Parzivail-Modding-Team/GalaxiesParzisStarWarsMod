package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.util.math.Ease;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class RigR2 extends ModelRig<AstromechEntity>
{
	public static final RigR2 INSTANCE = new RigR2();

	private RigR2()
	{
		super(Resources.id("rigs/droid/r2.p3dr"));
	}

	public Matrix4f getPartTransformation(AstromechEntity target, String part, float tickDelta)
	{
		var m = new Matrix4f();
		m.loadIdentity();

		var t = target.getLegDeltaExtension(tickDelta);

		var fullyExtendedLeg = 6.315f / 16;
		var legVisibleExt = 0.8f / 16;

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
			case "left_leg" -> m.multiply(new Quaternion(Vec3f.POSITIVE_X, -pushAngle, true));
			case "right_foot" -> m.multiply(new Quaternion(Vec3f.POSITIVE_X, pushAngle, true));
			case "right_shoulder" -> m.multiply(new Quaternion(Vec3f.POSITIVE_X, -bodyAngle, true));
			case "center_leg" -> m.multiplyByTranslation(0, -centerLegExtension, 0);
			case "center_foot" -> m.multiply(new Quaternion(Vec3f.POSITIVE_X, centerFootAngle, true));
			case "body" -> m.multiply(new Quaternion(Vec3f.POSITIVE_X, bodyAngle, true));
			case "head" -> m.multiply(new Quaternion(Vec3f.POSITIVE_Y, -headYaw, true));
		}

		return m;
	}
}
