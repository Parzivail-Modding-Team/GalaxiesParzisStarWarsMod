package com.parzivail.aurek.model.nemi;

import java.util.Objects;

public final class NemiVec3
{
	private final double x;
	private final double y;
	private final double z;

	public NemiVec3(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double x()
	{
		return x;
	}

	public double y()
	{
		return y;
	}

	public double z()
	{
		return z;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiVec3)obj;
		return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) &&
		       Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y) &&
		       Double.doubleToLongBits(this.z) == Double.doubleToLongBits(that.z);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
