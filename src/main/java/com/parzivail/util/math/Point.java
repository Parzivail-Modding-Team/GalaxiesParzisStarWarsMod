package com.parzivail.util.math;

public record Point(int x, int y)
{
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		var point = (Point)o;

		if (x != point.x)
			return false;
		return y == point.y;
	}

	@Override
	public int hashCode()
	{
		var result = x;
		result = 31 * result + y;
		return result;
	}
}
