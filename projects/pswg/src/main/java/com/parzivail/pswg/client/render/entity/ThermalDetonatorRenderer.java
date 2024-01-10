package com.parzivail.pswg.client.render.entity;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class ThermalDetonatorRenderer extends EntityRenderer<ThermalDetonatorEntity>
{
	public final Identifier TEXTURE = Resources.id("textures/item/model/thermal_detonator/thermal_detonator.png");
	public final Identifier TEXTURE2 = Resources.id("item/thermal_detonator/thermal_detonator");
	public final Identifier TEXTURE_PRIMED = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_primed_entity.png");
	public final Identifier TEXTURE_INBETWEEN = Resources.id("textures/item/model/thermal_detonator/thermal_detonator_inbetween_entity.png");

	public P3dModel model;

	public ThermalDetonatorRenderer(EntityRendererFactory.Context context)
	{
		super(context);
	}

	@Override
	public void render(ThermalDetonatorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity)));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw(tickDelta)));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getPitch(tickDelta)));
		if (model == null)
		{
			model = P3dManager.INSTANCE.get(TEXTURE2);
		}
		model.render(matrices, vertexConsumer, entity, null, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(ThermalDetonatorEntity entity)
	{
		if (entity.isPrimed())
		{
			int phase = entity.texturePhase / 2;
			if (phase == 0)
			{
				return TEXTURE_PRIMED;
			}
			else if (phase == 1)
			{
				return TEXTURE_INBETWEEN;
			}
		}
		return TEXTURE;
	}
}
