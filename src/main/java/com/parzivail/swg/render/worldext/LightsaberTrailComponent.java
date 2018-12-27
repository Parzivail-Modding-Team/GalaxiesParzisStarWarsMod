package com.parzivail.swg.render.worldext;

import com.parzivail.util.math.lwjgl.Vector3f;
import org.lwjgl.Sys;

public class LightsaberTrailComponent
{
	private final long createTime;
	private final int life;
	private final int color;
	private final Vector3f pBase;
	private final Vector3f pEnd;

	public LightsaberTrailComponent(int color, int life, Vector3f pBase, Vector3f pEnd)
	{
		this.life = life;
		createTime = Sys.getTime();
		this.color = color;
		this.pBase = pBase;
		this.pEnd = pEnd;
	}

	public boolean shouldDie()
	{
		return createTime + life < Sys.getTime();
	}

	public Vector3f getBasePos()
	{
		return pBase;
	}

	public Vector3f getEndPos()
	{
		return pEnd;
	}

	public int getColor()
	{
		return color;
	}
}
