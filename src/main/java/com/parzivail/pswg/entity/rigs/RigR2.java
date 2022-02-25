package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.droid.AstromechEntity;
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

		var bodyAngle = 0f;
		var tiltAngle = 30f;

		if (part.equals("body"))
			m.multiply(new Quaternion(Vec3f.POSITIVE_X, bodyAngle, true));
		else if (part.equals("left_shoulder") || part.equals("right_shoulder"))
			m.multiply(new Quaternion(Vec3f.POSITIVE_X, -bodyAngle - tiltAngle, true));
		else if (part.equals("left_foot") || part.equals("right_foot"))
			m.multiply(new Quaternion(Vec3f.POSITIVE_X, tiltAngle, true));

		return m;
	}
}
