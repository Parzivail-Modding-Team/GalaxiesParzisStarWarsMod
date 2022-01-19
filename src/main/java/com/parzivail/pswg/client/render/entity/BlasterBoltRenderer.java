package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class BlasterBoltRenderer extends EntityRenderer<BlasterBoltEntity>
{
	public BlasterBoltRenderer(EntityRendererFactory.Context ctx)
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

		var bYaw = (float)Math.atan2(velocity.x, velocity.z);
		var bPitch = (float)Math.asin(velocity.y);

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		Double ownerDist = null;
		var ownerSide = 1;

		var mc = MinecraftClient.getInstance();
		if (entity.getOwner() == mc.player)
		{
			var dist = mc.player.getCameraPosVec(tickDelta).distanceTo(entity.getLerpedPos(tickDelta));
			ownerDist = dist;

			if (mc.options.mainArm == Arm.LEFT)
				ownerSide = -1;

			var s = (float)MathHelper.clamp(dist, 0, 1);
			matrices.scale(s, s, s);
		}

		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(bYaw - MathHelper.PI / 2));
		matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(bPitch - MathHelper.PI / 2));

		if (ownerDist != null)
		{
			var d = 1 - MathHelper.clamp(ownerDist / 10, 0, 1);
			matrices.translate(0.2f * d, 0, 0.4f * d * ownerSide);
		}

		EnergyRenderer.renderEnergy(ModelTransformation.Mode.NONE, matrices, consumerProvider, light, 0xFFFFFF, false, 1.5f, 1, false, entity.getHue(), 1, 1);

		matrices.pop();
	}
}
