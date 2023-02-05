package com.parzivail.pswg.client.render.pm3d;

import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

public record PM3DLod(Identifier identifier, Vector3f[] verts,
                      Vector3f[] normals, Vector3f[] uvs,
                      PM3DObject[] objects, Box bounds)
{

	public void render(VertexConsumerBuffer vcb)
	{
		for (var o : objects)
			for (PM3DFace face : o.faces())
				emitFace(vcb, face);
	}

	private void emitFace(VertexConsumerBuffer vcb, PM3DFace face)
	{
		var a = face.verts.get(0);
		var b = face.verts.get(1);
		var c = face.verts.get(2);
		var d = face.verts.size() == 4 ? face.verts.get(3) : c;

		var tA = uvs[a.texture()];
		var tB = uvs[b.texture()];
		var tC = uvs[c.texture()];
		var tD = uvs[d.texture()];

		vcb.vertex(verts[a.vertex()], normals[a.normal()], tA.x, 1 - tA.y);
		vcb.vertex(verts[b.vertex()], normals[b.normal()], tB.x, 1 - tB.y);
		vcb.vertex(verts[c.vertex()], normals[c.normal()], tC.x, 1 - tC.y);
		vcb.vertex(verts[d.vertex()], normals[d.normal()], tD.x, 1 - tD.y);
	}
}
