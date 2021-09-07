package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.client.render.p3d.P3dMesh;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.math.Transform;
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
import net.minecraft.util.math.Vector4f;

public class T65BXwingRenderer extends ShipRenderer<T65BXwing>
{
//	private final Supplier<PR3Model<T65BXwing, RigT65B.Part>> model;

	public T65BXwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
//		model = Suppliers.memoize(() -> new PR3Model<>(PR3File.tryLoad(Resources.id("models/ship/xwing_t65b.pr3")), RigT65B.Part.class, RigT65B.INSTANCE::transform));
	}

	@Override
	protected void renderModel(T65BXwing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var mc = MinecraftClient.getInstance();
		var isClientShip = ShipEntity.getShip(mc.player) == entity;

		var modelRef = Client.ResourceManagers.getP3dManager().get(Resources.id("ship/xwing_t65b_test_with_sockets"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));
		for (var mesh : modelRef.meshes.values())
			renderMesh(matrix, entity, light, vertexConsumer, mesh, tickDelta);
	}

	private void renderMesh(MatrixStack matrix, T65BXwing entity, int light, VertexConsumer vertexConsumer, P3dMesh o, float tickDelta)
	{
		matrix.push();

		var entry = matrix.peek();

		entry.getNormal().multiply(new Matrix3f(o.transform));
		entry.getModel().multiply(o.transform);

		var modelMat = entry.getModel();
		var normalMat = entry.getNormal();

		var t = new Transform();
		RigT65B.INSTANCE.transform(t, entity, o.name, tickDelta);
		matrix.method_34425(t.value().getModel());

		for (var face : o.faces)
		{
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[0], face.normal[0], face.texture[0]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[1], face.normal[1], face.texture[1]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[2], face.normal[2], face.texture[2]);
			emitVertex(light, vertexConsumer, modelMat, normalMat, face.positions[3], face.normal[3], face.texture[3]);
		}

		for (var mesh : o.children)
			renderMesh(matrix, entity, light, vertexConsumer, mesh, tickDelta);

		matrix.pop();
	}

	private void emitVertex(int light, VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix, Vec3f vertex, Vec3f normal, Vec3f texCoord)
	{
		var v = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0F);
		v.transform(modelMatrix);
		var n = new Vec3f(normal.getX(), normal.getY(), normal.getZ());
		n.transform(normalMatrix);
		vertexConsumer.vertex(v.getX(), v.getY(), v.getZ(), 1, 1, 1, 1, texCoord.getX(), 1 - texCoord.getY(), OverlayTexture.DEFAULT_UV, light, n.getX(), n.getY(), n.getZ());
	}

	@Override
	public Identifier getTexture(T65BXwing entity)
	{
		return Resources.id("textures/ship/xwing_t65b.png");
	}
}
