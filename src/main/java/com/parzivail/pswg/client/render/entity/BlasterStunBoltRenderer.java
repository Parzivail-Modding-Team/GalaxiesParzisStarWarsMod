package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class BlasterStunBoltRenderer extends EntityRenderer<BlasterBoltEntity>
{
	public BlasterStunBoltRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}

	@Override
	public Identifier getTexture(BlasterBoltEntity entity)
	{
		return Resources.id("missing");
	}

	@Override
	public void render(BlasterBoltEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumerProvider, int light)
	{
		//		super.render(entity, yaw, tickDelta, matrices, consumerProvider, light);

		var velocity = entity.getVelocity();
		velocity = velocity.normalize();

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		var rPitch = (float)Math.asin(-velocity.y);
		var rYaw = (float)Math.atan2(velocity.x, velocity.z);

		matrices.multiply(new Quaternion(0, rYaw, 0, false));
		matrices.multiply(new Quaternion(rPitch, 0, 0, false));

		var age = entity.age + tickDelta;
		var size = age / 5f;

		EnergyRenderer.renderStunEnergy(ModelTransformation.Mode.NONE, matrices, consumerProvider, light, 0xFFFFFF, size, velocity, 0.6f);

		matrices.pop();
	}
}
