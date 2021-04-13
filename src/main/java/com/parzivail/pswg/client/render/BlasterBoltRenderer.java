package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class BlasterBoltRenderer extends EntityRenderer<BlasterBoltEntity>
{
	public BlasterBoltRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
	}

	@Override
	public Identifier getTexture(BlasterBoltEntity entity)
	{
		return Resources.identifier("missing");
	}

	@Override
	public void render(BlasterBoltEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumerProvider, int light)
	{
		//		super.render(entity, yaw, tickDelta, matrices, consumerProvider, light);

		Vec3d velocity = entity.getVelocity();
		velocity = velocity.normalize();

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		float s = MathHelper.clamp(entity.age / 2f,0.1f, 1);
		matrices.scale(s, s, s);

		float rPitch = (float)Math.asin(-velocity.y);
		float rYaw = (float)Math.atan2(velocity.x, velocity.z);

		matrices.multiply(new Quaternion(0, rYaw, 0, false));
		matrices.multiply(new Quaternion((float)(rPitch + Math.PI / 2), 0, 0, false));

		LightsaberRenderer.renderBlade(ModelTransformation.Mode.NONE, matrices, consumerProvider, light, 0xFFFFFF, false, 1.5f, 1, false, 0xFFFFFF, 0xFF0020);

		matrices.pop();
	}
}
