package com.parzivail.pswg.client.pm3d;

import java.util.ArrayList;

public class PM3DFace
{
	public final ArrayList<PM3DVertPointer> verts;
	public final byte material;

	public PM3DFace(byte material)
	{
		this.material = material;
		verts = new ArrayList<>();
	}
}
