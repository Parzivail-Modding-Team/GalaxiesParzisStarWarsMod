package com.parzivail.pswg.client.pr3;

import com.parzivail.util.math.Transform;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public record PR3Model<T, PT extends Enum<PT>>(PR3File container,
                                               Class<PT> partClass,
                                               com.parzivail.pswg.client.pr3.PR3Model.TransformerFunction<T, PT> transformer)
{
	@FunctionalInterface
	public interface TransformerFunction<T1, P extends Enum<P>>
	{
		void apply(Transform stack, T1 target, P part, float tickDelta);
	}

	private void emitFace(VertexConsumer vertexConsumer, MatrixStack.Entry matrices, PR3RenderedObject o, PR3FacePointer face, int light)
	{
		var modelMat = matrices.getModel();
		var normalMat = matrices.getNormal();

		var vA = o.vertices[face.a()];
		var vB = o.vertices[face.b()];
		var vC = o.vertices[face.c()];

		var nA = o.normals[face.a()];
		var nB = o.normals[face.b()];
		var nC = o.normals[face.c()];

		var tA = o.uvs[face.a()];
		var tB = o.uvs[face.b()];
		var tC = o.uvs[face.c()];

		emitVertex(light, vertexConsumer, modelMat, normalMat, vC, nC, tC);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vC, nC, tC);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vB, nB, tB);
		emitVertex(light, vertexConsumer, modelMat, normalMat, vA, nA, tA);
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord)
	{
		var v = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F);
		v.transform(modelMatrix);
		var n = new Vec3f(-normal.getX(), -normal.getY(), -normal.getZ());
		n.transform(normalMatrix);
		vertexConsumer.vertex(v.getX(), v.getY(), v.getZ(), 1, 1, 1, 1, texCoord.getX(), 1 - texCoord.getY(), OverlayTexture.DEFAULT_UV, light, n.getX(), n.getY(), n.getZ());
	}

	public void render(T target, VertexConsumerProvider vertexConsumers, Identifier texture, MatrixStack matrices, int light, float tickDelta)
	{
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));

		for (var o : getObjects())
			renderObject(target, o, vertexConsumer, matrices, tickDelta, light);
	}

	public void renderObject(VertexConsumer consumer, String objectName, T target, float tickDelta, MatrixStack matrices, int light)
	{
		var o = getObject(objectName);
		renderObject(target, o, consumer, matrices, tickDelta, light);
	}

	public void renderObject(T target, PR3RenderedObject o, VertexConsumer consumer, MatrixStack matrices, float tickDelta, int light)
	{
		matrices.push();

		var entry = matrices.peek();
		var modelMat = entry.getModel();
		modelMat.multiply(o.transformationMatrix);

		var t = new Transform();
		transform(t, target, o.name, tickDelta);

		matrices.method_34425(t.value().getModel());

		for (var face : o.faces)
			emitFace(consumer, matrices.peek(), o, face, light);

		matrices.pop();
	}

	public void transform(Transform stack, T target, String objectName, float tickDelta)
	{
		transformer.apply(stack, target, PT.valueOf(partClass, objectName), tickDelta);
	}

	public PR3RenderedObject getObject(String name)
	{
		for (var object : getObjects())
			if (object.name.equals(name))
				return object;

		return null;
	}

	public PR3RenderedObject[] getObjects()
	{
		return container.objects();
	}
}
