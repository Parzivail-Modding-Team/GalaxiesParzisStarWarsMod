package com.parzivail.pswg.client.render.entity.ship;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.ShipRenderer;
import com.parzivail.pswg.entity.rigs.RigZephyrJ;
import com.parzivail.pswg.entity.ship.SpeederEntity;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ZephyrJRenderer extends ShipRenderer<SpeederEntity>
{
	public ZephyrJRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	protected void renderModel(SpeederEntity entity, float yaw, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light)
	{
		var modelRef = P3dManager.INSTANCE.get(Resources.id("ship/zephyr_j"));
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));

		matrix.push();
		matrix.multiplyPositionMatrix(MathUtil.MAT4_SCALE_10_16THS);
		modelRef.render(matrix, vertexConsumer, entity, RigZephyrJ.INSTANCE::getPartTransformation, light, tickDelta, 255, 255, 255, 255);
		matrix.pop();
	}

	@Override
	public Identifier getTexture(SpeederEntity entity)
	{
		return Resources.id("textures/ship/zephyr_j.png");
	}
}
