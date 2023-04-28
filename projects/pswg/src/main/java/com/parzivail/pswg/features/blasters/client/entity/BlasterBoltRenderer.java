package com.parzivail.pswg.features.blasters.client.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.entity.EnergyRenderer;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.data.BlasterTag;
import com.parzivail.util.math.Ease;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

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
		var velocity = entity.getVelocity();
		velocity = velocity.normalize();

		var bYaw = (float)Math.atan2(velocity.x, velocity.z);
		var bPitch = (float)Math.asin(velocity.y);

		var sourceArm = entity.getSourceArm();
		if (entity.age == 0 && sourceArm.isEmpty())
			return;

		matrices.push();

		matrices.translate(0, 0.5f * entity.getHeight(), 0);

		var mc = MinecraftClient.getInstance();
		var isOwnedByClient = entity.getOwner() == mc.player;
		var isFirstPerson = mc.options.getPerspective() == Perspective.FIRST_PERSON;
		var isCameraPlayer = isFirstPerson && mc.getCameraEntity() == mc.player;
		var shouldScale = isOwnedByClient && isFirstPerson && isCameraPlayer;
		var shouldOffset = shouldScale;

		if (shouldScale)
		{
			var mainStack = mc.player.getInventory().getMainHandStack();
			if (mainStack.getItem() instanceof BlasterItem)
			{
				var bt = new BlasterTag(mainStack.getOrCreateNbt());
				shouldOffset = !bt.isAimingDownSights;
			}
		}

		var ownerDist = 0d;
		if (shouldScale)
		{
			var dist = mc.player.getCameraPosVec(tickDelta).distanceTo(entity.getLerpedPos(tickDelta));
			ownerDist = dist;

			var s = (float)MathHelper.clamp(dist / 1.5, 0, 1);
			matrices.scale(s, s, s);
		}

		if (ownerDist > 0)
		{
			matrices.multiply(new Quaternionf().rotationY(bYaw - MathHelper.PI / 2));
			matrices.multiply(new Quaternionf().rotationZ(bPitch - MathHelper.PI / 2));

			if (shouldOffset && sourceArm.isPresent())
			{
				var side = 1;
				if (sourceArm.get() == Arm.LEFT)
					side = -1;

				var d = 1 - Ease.outCubic((float)MathHelper.clamp(ownerDist / 15, 0, 1));
				matrices.translate(0.2f * d, 0, 0.5f * d * side);
			}

			EnergyRenderer.renderEnergy(ModelTransformationMode.NONE, matrices, consumerProvider, light, 0xFFFFFF, false, 1.5f, entity.getLength(), entity.getRadius(), false, entity.getColor());
		}
		matrices.pop();
	}
}
