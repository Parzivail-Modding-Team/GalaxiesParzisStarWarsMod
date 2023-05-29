package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.features.lightsabers.client.ThrownLightsaberEntity;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class ThrownLightsaberRenderer extends EntityRenderer<ThrownLightsaberEntity>
{
	private final ItemRenderer itemRenderer;

	public ThrownLightsaberRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
	}

	@Override
	public Identifier getTexture(ThrownLightsaberEntity entity)
	{
		return Resources.id("missing");
	}

	@Override
	public void render(ThrownLightsaberEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumerProvider, int light)
	{
		//		super.render(entity, yaw, tickDelta, matrices, consumerProvider, light);

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		var velocity = entity.getVelocity();
		velocity = velocity.normalize();

		var bYaw = (float)Math.atan2(velocity.x, velocity.z);
		var bPitch = (float)Math.asin(velocity.y);

		matrices.multiply(new Quaternionf().rotationY(bYaw));
		matrices.multiply(new Quaternionf().rotationX((float)(Math.PI / 2) - bPitch));

		matrices.multiply(new Quaternionf().rotationZ(MathUtil.toRadians(-(entity.age + tickDelta) * 31)));

		// TODO
		//		var lt = entity.getLightsaberData();
		//		lt.active = true;
		//		lt.serializeAsSubtag(stack);
		//
		//		this.itemRenderer.renderItem(stack, ModelTransformationMode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, consumerProvider, entity.getWorld(), 0);

		matrices.pop();
	}
}
