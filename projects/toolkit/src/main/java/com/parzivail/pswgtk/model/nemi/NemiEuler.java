package com.parzivail.pswgtk.model.nemi;

import java.util.Objects;

public final class NemiEuler
{
	private final double pitch;
	private final double yaw;
	private final double roll;

	public NemiEuler(double pitch, double yaw, double roll)
	{
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public double pitch()
	{
		return pitch;
	}

	public double yaw()
	{
		return yaw;
	}

	public double roll()
	{
		return roll;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (NemiEuler)obj;
		return Double.doubleToLongBits(this.pitch) == Double.doubleToLongBits(that.pitch) &&
		       Double.doubleToLongBits(this.yaw) == Double.doubleToLongBits(that.yaw) &&
		       Double.doubleToLongBits(this.roll) == Double.doubleToLongBits(that.roll);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(pitch, yaw, roll);
	}

	@Override
	public String toString()
	{
		return "NemiRotation[" +
		       "pitch=" + pitch + ", " +
		       "yaw=" + yaw + ", " +
		       "roll=" + roll + ']';
	}
}
