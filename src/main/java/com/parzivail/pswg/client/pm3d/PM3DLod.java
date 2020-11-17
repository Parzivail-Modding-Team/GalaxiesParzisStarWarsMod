package com.parzivail.pswg.client.pm3d;

import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class PM3DLod
{
	public final Identifier identifier;
	public final Vector3f[] verts;
	public final Vector3f[] normals;
	public final Vector3f[] uvs;
	public final PM3DObject[] objects;
	public final Box bounds;

	public PM3DLod(Identifier identifier, Vector3f[] verts, Vector3f[] normals, Vector3f[] uvs, PM3DObject[] objects, Box bounds)
	{
		this.identifier = identifier;
		this.verts = verts;
		this.normals = normals;
		this.uvs = uvs;
		this.objects = objects;
		this.bounds = bounds;
	}

	public void render(VertexConsumerBuffer vcb)
	{
		for (PM3DObject o : objects)
			for (PM3DFace face : o.faces)
				emitFace(vcb, face);
	}

	private void emitFace(VertexConsumerBuffer vcb, PM3DFace face)
	{
		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vector3f tA = uvs[a.texture];
		Vector3f tB = uvs[b.texture];
		Vector3f tC = uvs[c.texture];
		Vector3f tD = uvs[d.texture];

		vcb.vertex(verts[a.vertex], normals[a.normal], tA.getX(), 1 - tA.getY());
		vcb.vertex(verts[b.vertex], normals[b.normal], tB.getX(), 1 - tB.getY());
		vcb.vertex(verts[c.vertex], normals[c.normal], tC.getX(), 1 - tC.getY());
		vcb.vertex(verts[d.vertex], normals[d.normal], tD.getX(), 1 - tD.getY());
	}
}
