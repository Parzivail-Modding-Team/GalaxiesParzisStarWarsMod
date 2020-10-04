package com.parzivail.pswg.client.pm3d;

public class PM3DVertPointer
{
	public final int vertex;
	public final int normal;
	public final int texture;

	public PM3DVertPointer(int vertex, int normal, int texture)
	{
		this.vertex = vertex;
		this.normal = normal;
		this.texture = texture;
	}
}
