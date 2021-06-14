package com.parzivail.pswg.client.render.ship;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pr3.PR3File;
import com.parzivail.pswg.client.pr3.PR3Model;
import com.parzivail.pswg.client.render.ShipRenderer;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

public class T65BXwingRenderer extends ShipRenderer<T65BXwing>
{
	private final Lazy<PR3Model<T65BXwing, RigT65B.Part>> model;

	public T65BXwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		model = new Lazy<>(() -> new PR3Model<>(PR3File.tryLoad(Resources.id("models/ship/xwing_t65b.pr3")), RigT65B.Part.class, RigT65B.INSTANCE::transform));
	}

	@Override
	protected void renderModel(T65BXwing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var mc = MinecraftClient.getInstance();
		var isClientShip = ShipEntity.getShip(mc.player) == entity;

		var modelRef = model.get();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));

		var eyeOffset = -0.6f;
		matrix.translate(0, eyeOffset, 0);

		for (var o : modelRef.getObjects())
		{
			// TODO
			if (isClientShip && (RigT65B.Part.Cockpit.is(o)))
				continue;

			modelRef.renderObject(entity, o, vertexConsumer, matrix, tickDelta, light);
		}
	}

	@Override
	public Identifier getTexture(T65BXwing entity)
	{
		return Resources.id("textures/ship/xwing_t65b.png");
	}
}
