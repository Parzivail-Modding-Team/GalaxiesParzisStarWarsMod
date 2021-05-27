package com.parzivail.pswg.client.pr3;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class PR3Model<T, PT extends Enum<PT>>
{
	@FunctionalInterface
	public interface TransformerFunction<T1, P extends Enum<P>>
	{
		void apply(MatrixStack stack, T1 target, P part, float tickDelta);
	}

	protected final PR3File container;
	protected final Class<PT> partClass;
	protected final TransformerFunction<T, PT> transformer;

	public PR3Model(PR3File container, Class<PT> partClass, TransformerFunction<T, PT> transformer)
	{
		this.container = container;
		this.partClass = partClass;
		this.transformer = transformer;
	}

	private void emitFace(VertexConsumer vertexConsumer, MatrixStack.Entry matrices, PR3Object o, PR3FacePointer face, int light)
	{
		Matrix4f modelMat = matrices.getModel();
		Matrix3f normalMat = matrices.getNormal();

		Vec3f vA = o.vertices[face.a];
		Vec3f vB = o.vertices[face.b];
		Vec3f vC = o.vertices[face.c];

		Vec3f nA = o.normals[face.a];
		Vec3f nB = o.normals[face.b];
		Vec3f nC = o.normals[face.c];

		Vec3f tA = o.uvs[face.a];
		Vec3f tB = o.uvs[face.b];
		Vec3f tC = o.uvs[face.c];

		emitVertex(light, vertexConsumer, modelMat, normalMat, vC, nC, tC);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vC, nC, tC);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vB, nB, tB);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vA, nA, tA);
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord)
	{
		Vector4f v = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F);
		v.transform(modelMatrix);
		Vec3f n = new Vec3f(-normal.getX(), -normal.getY(), -normal.getZ());
		n.transform(normalMatrix);
		vertexConsumer.vertex(v.getX(), v.getY(), v.getZ(), 1, 1, 1, 1, texCoord.getX(), 1 - texCoord.getY(), 0xFFFFFF, light, n.getX(), n.getY(), n.getZ());
	}

	public void render(T target, VertexConsumerProvider vertexConsumers, Identifier texture, MatrixStack matrices, int light, float tickDelta)
	{
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));

		for (PR3Object o : getObjects())
			renderObject(target, o, vertexConsumer, matrices, tickDelta, light);
	}

	public void renderObject(VertexConsumer consumer, String objectName, T target, float tickDelta, MatrixStack matrices, int light)
	{
		PR3Object o = getObject(objectName);
		renderObject(target, o, consumer, matrices, tickDelta, light);
	}

	private void renderObject(T target, PR3Object o, VertexConsumer consumer, MatrixStack matrices, float tickDelta, int light)
	{
		matrices.push();

		MatrixStack.Entry entry = matrices.peek();
		Matrix4f modelMat = entry.getModel();
		modelMat.multiply(o.transformationMatrix);

		transform(matrices, target, o.name, tickDelta);

		for (PR3FacePointer face : o.faces)
			emitFace(consumer, matrices.peek(), o, face, light);

		matrices.pop();
	}

	public void transform(MatrixStack stack, T target, String objectName, float tickDelta)
	{
		transformer.apply(stack, target, PT.valueOf(partClass, objectName), tickDelta);
	}

	public PR3Object getObject(String name)
	{
		for (PR3Object object : getObjects())
			if (object.name.equals(name))
				return object;

		return null;
	}

	public PR3Object[] getObjects()
	{
		return container.objects;
	}
}
