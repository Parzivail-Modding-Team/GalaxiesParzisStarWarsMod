package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import net.minecraft.util.math.Matrix4f;

public class RigZephyrJ extends ModelRig<SpeederEntity>
{
	public static final RigZephyrJ INSTANCE = new RigZephyrJ();

	private RigZephyrJ()
	{
		super(Resources.id("rigs/ship/zephyr_j.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(SpeederEntity target, String part, float tickDelta)
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		return matrix4f;
	}
}
