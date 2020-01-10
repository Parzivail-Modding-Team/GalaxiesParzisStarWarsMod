package com.parzivail.pswg.client.pm3d;

import java.util.ArrayList;

public class PM3DObject
{
	public final String objName;
	public final String matName;
	public final ArrayList<PM3DFace> faces;

	public PM3DObject(String objName, String matName, ArrayList<PM3DFace> faces)
	{
		this.objName = objName;
		this.matName = matName;
		this.faces = faces;
	}
}
