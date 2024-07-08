package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.RZ1Awing;
import com.parzivail.util.generics.Make;
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
