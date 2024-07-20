package com.parzivail.aurek.model.nemi;

public record NemiVec3(double x, double y, double z)
{
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
