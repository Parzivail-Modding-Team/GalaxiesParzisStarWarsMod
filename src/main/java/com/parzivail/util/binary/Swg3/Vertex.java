package com.parzivail.util.binary.Swg3;

import com.parzivail.util.math.lwjgl.Vector2f;
import com.parzivail.util.math.lwjgl.Vector3f;

/**
 * Created by colby on 12/25/2017.
 */
public class Vertex
{
	public Vector3f position;
	public Vector3f normal;
	public Vector2f textureCoord;

	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord)
	{
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
	}
}
