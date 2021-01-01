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

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		double d3 = MathHelper.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
		float rYaw = (float)(Math.atan2(velocity.z, velocity.x) * 180.0D / Math.PI) - 90.0F;
		float rPitch = (float)(-(Math.atan2(velocity.y, d3) * 180.0D / Math.PI));

		matrices.multiply(new Quaternion(0, -rYaw, 0, true));
		matrices.multiply(new Quaternion(rPitch + 90, 0, 0, true));

		LightsaberRenderer.renderBlade(ModelTransformation.Mode.NONE, matrices, consumerProvider, light, 0xFFFFFF, false, 1.5f, 1, false, 0xFFFFFF, 0xFF0020, false);

		matrices.pop();
	}
}
