package com.parzivail.util.binary.Swg3;

import com.parzivail.util.math.lwjgl.Vector3f;

/**
 * Created by colby on 12/25/2017.
 */
public class SwgTag
{
	public String name;
	public Vector3f origin;

	public SwgTag(String tagName, Vector3f origin)
	{
		name = tagName;
		this.origin = origin;
	}
}
