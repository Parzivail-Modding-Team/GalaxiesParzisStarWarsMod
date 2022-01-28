package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.entity.rigs.RigT65B;
import com.parzivail.pswg.entity.ship.T65BXwing;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class T65BXwingRenderer extends ShipRenderer<T65BXwing>
{
	public T65BXwingRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	protected void renderModel(T65BXwing entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var modelRef = P3dManager.INSTANCE.get(Resources.id("ship/xwing_t65b"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));

		modelRef.render(matrix, vertexConsumer, entity, RigT65B.INSTANCE::getPartTransformation, light, tickDelta);
	}

	@Override
	public Identifier getTexture(T65BXwing entity)
	{
		return Resources.id("textures/ship/xwing_t65b.png");
	}
}
