package com.parzivail.pswg.client.render.entity.ship;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.client.render.pr3.PR3File;
import com.parzivail.pswg.client.render.pr3.PR3Model;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class T65BXwingRenderer extends ShipRenderer<T65BXwing>
{
	private final Supplier<PR3Model<T65BXwing, RigT65B.Part>> model;

	public T65BXwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		model = Suppliers.memoize(() -> new PR3Model<>(PR3File.tryLoad(Resources.id("models/ship/xwing_t65b.pr3")), RigT65B.Part.class, RigT65B.INSTANCE::transform));
	}

	@Override
	protected void renderModel(T65BXwing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var mc = MinecraftClient.getInstance();
		var isClientShip = ShipEntity.getShip(mc.player) == entity;

		var modelRef = model.get();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));
		for (var o : modelRef.getObjects())
		{
			modelRef.renderObject(entity, o, vertexConsumer, matrix, tickDelta, light);
		}
	}

	@Override
	public Identifier getTexture(T65BXwing entity)
	{
		return Resources.id("textures/ship/xwing_t65b.png");
	}
}
