package com.parzivail.pswg.client.pm3d;

public enum PM3DFlags
{
	None(1), AmbientOcclusion(2);

	private final int flag;

	PM3DFlags(int i)
	{
		flag = i;
	}

	public int getFlag()
	{
		return flag;
	}
}
