package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.RZ1Awing;
import org.joml.Matrix4f;

public class RigRZ1 extends ModelRig<RZ1Awing>
{
	public static final RigRZ1 INSTANCE = new RigRZ1();

	protected RigRZ1()
	{
		super(Resources.id("rigs/ship/awing_rz1.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(RZ1Awing target, String part, float tickDelta)
	{
		Matrix4f matrix4f = new Matrix4f();
		return matrix4f;
	}
}
