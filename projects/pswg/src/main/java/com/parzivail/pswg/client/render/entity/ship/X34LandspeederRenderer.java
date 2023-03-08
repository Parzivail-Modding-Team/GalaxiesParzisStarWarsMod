package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.entity.rigs.RigX34;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class X34LandspeederRenderer extends ShipRenderer<SpeederEntity>
{
	public X34LandspeederRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	protected void renderModel(SpeederEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var modelRef = P3dManager.INSTANCE.get(Resources.id("ship/landspeeder_x34"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));

		modelRef.render(matrix, vertexConsumer, entity, RigX34.INSTANCE::getPartTransformation, light, tickDelta, 255, 255, 255, 255);
	}

	@Override
	public Identifier getTexture(SpeederEntity entity)
	{
		return Resources.id("textures/ship/landspeeder_x34.png");
	}
}
