package com.parzivail.pm3d;

public enum Pm3dFlags
{
	None(1), AmbientOcclusion(2);

	private final int flag;

	Pm3dFlags(int i)
	{
		flag = i;
	}

	public int getFlag()
	{
		return flag;
	}
}
