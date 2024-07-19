package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.entity.rigs.RigRZ1;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.RZ1Awing;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RZ1AwingRenderer extends ShipRenderer<RZ1Awing>
{
	public RZ1AwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	protected void renderModel(RZ1Awing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var modelRef = P3dManager.INSTANCE.get(Resources.id("ship/awing_rz1"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));

		modelRef.render(matrix, vertexConsumer, entity, RigRZ1.INSTANCE::getPartTransformation, light, tickDelta, 255, 255, 255, 255);
	}
	@Override
	public Identifier getTexture(RZ1Awing entity)
	{
		return Resources.id("textures/ship/awing_rz1.png");
	}
}
