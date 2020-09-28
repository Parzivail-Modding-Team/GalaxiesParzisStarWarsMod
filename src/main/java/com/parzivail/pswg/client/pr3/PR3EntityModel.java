package com.parzivail.pswg.client.pr3;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class PR3EntityModel<T extends Entity>
{
	@FunctionalInterface
	public interface OrientationGetterFunction<T1>
	{
		Quaternion apply(T1 entity, String part, float tickDelta);
	}

	private final PR3File container;
	private final OrientationGetterFunction<T> orientationGetter;

	public PR3EntityModel(PR3File container, OrientationGetterFunction<T> orientationGetter)
	{
		this.container = container;
		this.orientationGetter = orientationGetter;
	}

	private void emitFace(T entity, Identifier texture, float tickDelta, MatrixStack.Entry matrices, VertexConsumerProvider vertexConsumers, int light, PR3Object o, PR3FacePointer face)
	{
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
		Matrix4f modelMat = matrices.getModel();
		Matrix3f normalMat = matrices.getNormal();

		modelMat.multiply(o.transformationMatrix);

		Quaternion q = orientationGetter.apply(entity, o.name, tickDelta);
		if (q != null)
		{
			modelMat.multiply(q);
			normalMat.multiply(q);
		}

		Vector3f vA = o.vertices[face.a];
		Vector3f vB = o.vertices[face.b];
		Vector3f vC = o.vertices[face.c];
		Vector3f vD = o.vertices[face.c];

		Vector3f nA = o.normals[face.a];
		Vector3f nB = o.normals[face.b];
		Vector3f nC = o.normals[face.c];
		Vector3f nD = o.normals[face.c];

		Vector3f tA = o.uvs[face.a];
		Vector3f tB = o.uvs[face.b];
		Vector3f tC = o.uvs[face.c];
		Vector3f tD = o.uvs[face.c];

		emitVertex(light, vertexConsumer, modelMat, normalMat, vD, nD, tD);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vC, nC, tC);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vB, nB, tB);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vA, nA, tA);
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vector3f vertex, Vector3f normal, Vector3f texCoord)
	{
		Vector4f v = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F);
		v.transform(modelMatrix);
		Vector3f n = new Vector3f(-normal.getX(), -normal.getY(), -normal.getZ());
		n.transform(normalMatrix);
		vertexConsumer.vertex(v.getX(), v.getY(), v.getZ(), 1, 1, 1, 1, texCoord.getX(), 1 - texCoord.getY(), 0xFFFFFF, light, n.getX(), n.getY(), n.getZ());
	}

	public void render(T entity, Identifier texture, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		for (PR3Object o : container.objects)
			for (PR3FacePointer face : o.faces)
			{
				matrices.push();
				emitFace(entity, texture, tickDelta, matrices.peek(), vertexConsumers, light, o, face);
				matrices.pop();
			}
	}
}
