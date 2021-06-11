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

public class LaaEntityRenderer extends MobEntityRenderer<FishEntity, SinglePartEntityModel<FishEntity>>
{
	public LaaEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, Client.ResourceManagers.getNemManager().getModel(Resources.id("mob/fish/laa"), LaaEntityRenderer::setAngles), 0.5f);
	}

	@Override
	public Identifier getTexture(FishEntity entity)
	{
		return Resources.id("textures/entity/fish/laa.png");
	}

	public static void setAngles(SinglePartEntityModel<FishEntity> model, FishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		if (entity.isAiDisabled())
			animationProgress = 0;

		var body = model.getPart().getChild("body");
		var pectoralFinLeft = body.getChild("pectoralFinLeft");
		var tail = body.getChild("tail");
		var tail1 = tail.getChild("tail_1");
		var flailTop = body.getChild("flailTop");
		var flailBottom = body.getChild("flailBottom");
		var pectoralFinRight = body.getChild("pectoralFinLeft_1");

		var f = 1.0F;
		if (!entity.isTouchingWater())
		{
			f = 1.5F;
		}

		var tailProgress = MathHelper.sin(0.6F * animationProgress);
		tail1.yaw = -f * 0.3F * tailProgress;
		tail.yaw = -f * 0.1F * tailProgress;

		var finProgress = MathHelper.sin(0.1F * animationProgress);
		pectoralFinLeft.yaw = -1f - f * 0.15F * finProgress;
		pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;

		flailTop.pitch = f * 0.05F * finProgress;
		flailBottom.pitch = -0.3490658503988659F - f * 0.05F * finProgress;
	}

	@Override
	protected void setupTransforms(FishEntity entity, MatrixStack matrixStack, float f, float g, float h)
	{
		super.setupTransforms(entity, matrixStack, f, g, h);

		if (entity.isAiDisabled())
			return;

		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!entity.isTouchingWater())
		{
			matrixStack.translate(0.10000000149011612D, 0.10000000149011612D, -0.10000000149011612D);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
