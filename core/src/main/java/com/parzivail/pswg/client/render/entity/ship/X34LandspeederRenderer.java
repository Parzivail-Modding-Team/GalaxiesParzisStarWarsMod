package com.parzivail.pswg.client.render.entity.ship;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.client.render.pr3.PR3File;
import com.parzivail.pswg.client.render.pr3.PR3Model;
import com.parzivail.pswg.entity.rigs.RigX34;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class X34LandspeederRenderer extends ShipRenderer<SpeederEntity>
{
	private final Supplier<PR3Model<SpeederEntity, RigX34.Part>> model;

	public X34LandspeederRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		model = Suppliers.memoize(() -> new PR3Model<>(PR3File.tryLoad(Resources.id("models/ship/landspeeder_x34.pr3")), RigX34.Part.class, RigX34.INSTANCE::transform));
	}

	@Override
	protected void renderModel(SpeederEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var mc = MinecraftClient.getInstance();
		var isClientShip = ShipEntity.getShip(mc.player) == entity;

		var modelRef = model.get();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		for (var o : modelRef.getObjects())
		{
			modelRef.renderObject(entity, o, vertexConsumer, matrix, tickDelta, light);
		}
	}

	@Override
	public Identifier getTexture(SpeederEntity entity)
	{
		return Resources.id("textures/ship/landspeeder_x34.png");
	}
}
