package com.parzivail.pswg.client.pm3d;

import java.util.ArrayList;

public class PM3DObject
{
	public final String objName;
	public final ArrayList<PM3DFace> faces;

	public PM3DObject(String objName, ArrayList<PM3DFace> faces)
	{
		this.objName = objName;
		this.faces = faces;
	}
}
