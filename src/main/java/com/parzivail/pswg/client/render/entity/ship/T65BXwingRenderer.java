package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.client.render.p3d.P3dObject;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class T65BXwingRenderer extends ShipRenderer<T65BXwing>
{
	public T65BXwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	protected void renderModel(T65BXwing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var mc = MinecraftClient.getInstance();

		var modelRef = Client.ResourceManagers.getP3dManager().get(Resources.id("ship/xwing_t65b_test_with_sockets"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));
		for (var mesh : modelRef.rootObjects)
			renderMesh(matrix, entity, light, vertexConsumer, mesh, tickDelta);
	}

	private void renderMesh(MatrixStack matrix, T65BXwing entity, int light, VertexConsumer vertexConsumer, P3dObject o, float tickDelta)
	{
		matrix.push();

		var entry = matrix.peek();
		entry.getNormalMatrix().multiply(new Matrix3f(o.transform));
		entry.getPositionMatrix().multiply(o.transform);

		matrix.multiplyPositionMatrix(RigT65B.INSTANCE.getPartTransformation(entity, o.name, tickDelta));

		var modelMat = entry.getPositionMatrix();
		var normalMat = entry.getNormalMatrix();

		for (var face : o.faces)
		{
			// TODO: remove this and disable smooth faces in blender before exporting
			var normal = new Vec3f();
			normal.add(face.normal[0]);
			normal.add(face.normal[1]);
			normal.add(face.normal[2]);
			normal.add(face.normal[3]);
			normal.normalize();

			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[0], normal, face.texture[0]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[1], normal, face.texture[1]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[2], normal, face.texture[2]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[3], normal, face.texture[3]);
		}

		for (var mesh : o.children)
			renderMesh(matrix, entity, light, vertexConsumer, mesh, tickDelta);

		matrix.pop();
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord)
	{
		vertexConsumer
				.vertex(modelMatrix, vertex.getX(), vertex.getY(), vertex.getZ())
				.color(255, 255, 255, 255)
				.texture(texCoord.getX(), 1 - texCoord.getY())
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(normalMatrix, normal.getX(), normal.getY(), normal.getZ())
				.next();
	}

	@Override
	public Identifier getTexture(T65BXwing entity)
	{
		return Resources.id("textures/ship/xwing_t65b.png");
	}
}
