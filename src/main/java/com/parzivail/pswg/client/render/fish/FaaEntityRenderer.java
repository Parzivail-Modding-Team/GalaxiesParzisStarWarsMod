package com.parzivail.pswg.client.render.fish;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class FaaEntityRenderer extends MobEntityRenderer<FishEntity, SinglePartEntityModel<FishEntity>>
{
	public FaaEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, Client.nemManager.getModel(Resources.identifier("mob/fish/faa"), FaaEntityRenderer::setAngles), 0.5f);
	}

	@Override
	public Identifier getTexture(FishEntity entity)
	{
		return Resources.identifier("textures/entity/fish/faa.png");
	}

	public static void setAngles(SinglePartEntityModel<FishEntity> model, FishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		if (entity.isAiDisabled())
			return;

		var body = model.getPart().getChild("body");
		var tailBase = body.getChild("tailBase");
		var tail = tailBase.getChild("tail");
		var caudalFin = tail.getChild("caudalFin");
		var pectoralFinRight = body.getChild("pectoralFinRight");
		var pectoralFinLeft = body.getChild("pectoralFinLeft");

		var f = 1.0F;
		if (!entity.isTouchingWater())
		{
			f = 1.5F;
		}

		var tailProgress = MathHelper.sin(0.6F * animationProgress);
		tailBase.yaw = -f * 0.15F * tailProgress;
		tail.yaw = -f * 0.15F * tailProgress;
		caudalFin.yaw = -f * 0.25F * tailProgress;

		var finProgress = MathHelper.sin(0.1F * animationProgress);
		pectoralFinLeft.yaw = -1f - f * 0.15F * finProgress;
		pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;
	}

	@Override
	protected void setupTransforms(FishEntity entity, MatrixStack matrixStack, float f, float g, float h)
	{
		super.setupTransforms(entity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!entity.isTouchingWater())
		{
			matrixStack.translate(0.10000000149011612D, 0.10000000149011612D, -0.10000000149011612D);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
