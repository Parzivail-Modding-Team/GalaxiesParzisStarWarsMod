package com.parzivail.util.math;

/**
 * Created by Colby on 5/15/2016.
 */
public class FPoint
{
	public float x, y, z;

	/**
	 * Creates a new 2D point in space
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public FPoint(float x, float y)
	{
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Creates a new 3D point in space
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public FPoint(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
