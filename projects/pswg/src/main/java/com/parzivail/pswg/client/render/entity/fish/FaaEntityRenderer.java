package com.parzivail.pswg.client.render.entity.fish;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

public class FaaEntityRenderer extends MobEntityRenderer<FishEntity, SinglePartEntityModel<FishEntity>>
{
	public FaaEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, NemManager.INSTANCE.getModel(Resources.id("mob/fish/faa"), FaaEntityRenderer::setAngles), 0.5f);
	}

	@Override
	public Identifier getTexture(FishEntity entity)
	{
		return Resources.id("textures/entity/fish/faa.png");
	}

	public static void setAngles(SinglePartEntityModel<FishEntity> model, FishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitc, float tickDelta)
	{
		if (entity.isAiDisabled())
			animationProgress = 0;

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

		if (entity.isAiDisabled())
			return;

		var i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(new Quaternionf().rotationY(MathUtil.toRadians(i)));
		if (!entity.isTouchingWater())
		{
			matrixStack.translate(0.1f, 0.1f, -0.1f);
			matrixStack.multiply(new Quaternionf().rotationZ((float)(Math.PI / 2)));
		}
	}
}
