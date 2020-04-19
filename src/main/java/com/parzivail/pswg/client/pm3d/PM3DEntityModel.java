package com.parzivail.pswg.client.pm3d;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class PM3DEntityModel
{
	private final PM3DFile container;

	public PM3DEntityModel(PM3DFile container)
	{
		this.container = container;
	}

	private void emitFace(Identifier texture, float tickDelta, MatrixStack.Entry matrices, VertexConsumerProvider vertexConsumers, int light, PM3DFace face)
	{
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
		Matrix4f matrix4f = matrices.getModel();
		Matrix3f matrix3f = matrices.getNormal();

		PM3DVertPointer a = face.verts.get(0);
		PM3DVertPointer b = face.verts.get(1);
		PM3DVertPointer c = face.verts.get(2);
		PM3DVertPointer d = face.verts.size() == 4 ? face.verts.get(3) : c;

		Vec3d vA = container.verts.get(a.getVertex());
		Vec3d vB = container.verts.get(b.getVertex());
		Vec3d vC = container.verts.get(c.getVertex());
		Vec3d vD = container.verts.get(d.getVertex());

		Vec3d nA = container.normals.get(a.getNormal());
		Vec3d nB = container.normals.get(b.getNormal());
		Vec3d nC = container.normals.get(c.getNormal());
		Vec3d nD = container.normals.get(d.getNormal());

		Vec2f tA = container.uvs.get(a.getTexture());
		Vec2f tB = container.uvs.get(b.getTexture());
		Vec2f tC = container.uvs.get(c.getTexture());
		Vec2f tD = container.uvs.get(d.getTexture());

		emitVertex(light, vertexConsumer, matrix4f, matrix3f, vA, nA, tA);
		emitVertex(light, vertexConsumer, matrix4f, matrix3f, vB, nB, tB);
		emitVertex(light, vertexConsumer, matrix4f, matrix3f, vC, nC, tC);
		emitVertex(light, vertexConsumer, matrix4f, matrix3f, vD, nD, tD);

		//		quadEmitter.pos(0, VertUtil.toMinecraftVertex(vA)).normal(0, VertUtil.toMinecraftNormal(nA)).sprite(0, 0, tA.x, 1 - tA.y);
		//		quadEmitter.pos(1, VertUtil.toMinecraftVertex(vB)).normal(1, VertUtil.toMinecraftNormal(nB)).sprite(1, 0, tB.x, 1 - tB.y);
		//		quadEmitter.pos(2, VertUtil.toMinecraftVertex(vC)).normal(2, VertUtil.toMinecraftNormal(nC)).sprite(2, 0, tC.x, 1 - tC.y);
		//		quadEmitter.pos(3, VertUtil.toMinecraftVertex(vD)).normal(3, VertUtil.toMinecraftNormal(nD)).sprite(3, 0, tD.x, 1 - tD.y);
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3d vertex, Vec3d normal, Vec2f texCoord)
	{
		Vector4f v = new Vector4f((float)vertex.x, (float)vertex.y, (float)vertex.z, 1.0F);
		v.transform(modelMatrix);
		Vector3f n = new Vector3f((float)normal.x, (float)normal.y, (float)normal.z);
		n.transform(normalMatrix);
		vertexConsumer.vertex(v.getX(), v.getY(), v.getZ(), 1, 1, 1, 1, texCoord.x, 1 - texCoord.y, 0xFFFFFF, light, n.getX(), n.getY(), n.getZ());
	}

	public void render(Identifier texture, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		for (PM3DObject o : container.objects)
			for (PM3DFace face : o.faces)
				emitFace(texture, tickDelta, matrices.peek(), vertexConsumers, light, face);
	}
}
