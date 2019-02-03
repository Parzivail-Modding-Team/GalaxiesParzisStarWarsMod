package com.parzivail.swg.render.worldext;

import com.parzivail.util.math.lwjgl.Vector3f;

public class LightsaberTrailComponent
{
	private final long createTime;
	private final int coreColor;
	private final float bladeLength;
	private final int life;
	private final int color;
	private final Vector3f pBase;
	private final Vector3f pEnd;

	public LightsaberTrailComponent(int bladeColor, int coreColor, float bladeLength, int life, Vector3f pBase, Vector3f pEnd)
	{
		this.coreColor = coreColor;
		this.bladeLength = bladeLength;
		this.life = life;
		createTime = System.currentTimeMillis();
		color = bladeColor;
		this.pBase = pBase;
		this.pEnd = pEnd;
	}

	public boolean shouldDie()
	{
		return createTime + life < System.currentTimeMillis();
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

	public int getCoreColor()
	{
		return coreColor;
	}

	public float getBladeLength()
	{
		return bladeLength;
	}
}
