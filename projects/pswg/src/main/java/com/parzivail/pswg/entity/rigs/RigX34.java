package com.parzivail.pswg.entity.rigs;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import org.joml.Matrix4f;

public class RigX34 extends ModelRig<SpeederEntity>
{
	public static final RigX34 INSTANCE = new RigX34();

	private RigX34()
	{
		super(Resources.id("rigs/ship/landspeeder_x34.p3dr"));
	}

	@Override
	public Matrix4f getPartTransformation(SpeederEntity target, String part, float tickDelta)
	{
		return new Matrix4f().scale(0.5f, 0.5f, 0.5f);
	}
}
