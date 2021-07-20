package com.parzivail.util.math;

public record SpriteSheetPoint(int x, int y, int sheet)
{
	public SpriteSheetPoint(int x, int y)
	{
		this(x, y, 0);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		var point = (SpriteSheetPoint)o;

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
