package com.parzivail.pswg.client.pm3d;

public class Pm3dVertPointer
{
	private final int vertex;
	private final int normal;
	private final int texture;

	public Pm3dVertPointer(int vertex, int normal, int texture)
	{
		this.vertex = vertex;
		this.normal = normal;
		this.texture = texture;
	}

	public int getVertex()
	{
		return vertex;
	}

	public int getNormal()
	{
		return normal;
	}

	public int getTexture()
	{
		return texture;
	}
}
