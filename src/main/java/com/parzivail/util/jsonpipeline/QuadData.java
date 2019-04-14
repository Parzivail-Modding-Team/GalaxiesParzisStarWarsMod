package com.parzivail.util.jsonpipeline;

public class QuadData
{
	public static final QuadData DEFAULT = new QuadData(false, -1);
	public final boolean lit;
	public final int lightColor;

	public QuadData(boolean lit, int lightColor)
	{
		this.lit = lit;
		this.lightColor = lightColor;
	}
}
