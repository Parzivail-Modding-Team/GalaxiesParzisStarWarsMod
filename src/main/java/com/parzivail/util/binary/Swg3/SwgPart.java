package com.parzivail.util.binary.Swg3;

/**
 * Created by colby on 12/25/2017.
 */
public class SwgPart
{
	public String name;
	public int flags;
	public SwgTexture[] textures;
	public FacePointer[] triangles;
	public SwgSt[] stPairs;
	public Vertex[][] verts;

	public SwgPart(String partName, int partFlags, SwgTexture[] textures, FacePointer[] triangles, SwgSt[] stPairs, Vertex[][] verts)
	{
		this.name = partName;
		this.flags = partFlags;
		this.textures = textures;
		this.triangles = triangles;
		this.stPairs = stPairs;
		this.verts = verts;
	}
}
