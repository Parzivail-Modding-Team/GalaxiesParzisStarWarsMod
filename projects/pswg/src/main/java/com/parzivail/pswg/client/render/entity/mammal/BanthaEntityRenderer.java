package com.parzivail.pswg.client.render.entity.mammal;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.mammal.BanthaEntity;
import com.parzivail.pswg.mixin.SinglePartEntityModelAccessor;
import com.parzivail.util.client.render.MutableAnimatedModel;
import com.parzivail.util.math.TwoJointIk;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BanthaEntityRenderer extends MobEntityRenderer<BanthaEntity, SinglePartEntityModel<BanthaEntity>>
{
	//	private final MutableAnimatedModel<BanthaEntity> model;

	public BanthaEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, Client.NEM_MANAGER.getModel(Resources.id("mob/mammal/bantha"), BanthaEntityRenderer::setAngles), 0.5f);
		//		model = NemManager.INSTANCE.getModel(Resources.id("mob/mammal/bantha"), BanthaEntityRenderer::setAngles);
	}

	private static void setAngles(MutableAnimatedModel<BanthaEntity> model, BanthaEntity entity, float v, float v1, float v2, float v3, float v4, float tickDelta)
	{
		model.getPart().traverse().forEach(ModelPart::resetTransform);
		SinglePartEntityModelAccessor accessor = (SinglePartEntityModelAccessor)model;
		accessor.callUpdateAnimation(entity.callingAnimationState, BanthaAnimations.bantha_call, v2, 1.0F);
		accessor.callUpdateAnimation(entity.forageAnimationState, BanthaAnimations.bantha_forage, v2, 1.0F);
	}

	private static void setLegIk(Entity entity, ModelPart body, String upperLegName, String lowerLegName, Vec3d legPosition)
	{
		var result = TwoJointIk.forwardEvaluate(entity, legPosition, false, entity.getYaw(), 0.5625f, 0.5625f);
		var upperLeg = body.getChild(upperLegName);
		var lowerLeg = upperLeg.getChild(lowerLegName);
		upperLeg.pitch = -(float)(result.hipPitch() / 180 * Math.PI);
		lowerLeg.pitch = (float)(result.kneePitch() / 180 * Math.PI);
	}

	@Override
	public Identifier getTexture(BanthaEntity entity)
	{
		return Resources.id("textures/entity/mammal/bantha.png");
	}

	//	@Override
	//	public void render(BanthaEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	//	{
	//		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	//
	//		var con = vertexConsumers.getBuffer(RenderLayer.getLines());
	//		var v = VertexConsumerBuffer.Instance;
	//		v.init(con, matrices.peek(), 1, 1, 1, 1, OverlayTexture.DEFAULT_UV, light);
	//
	////		var start = new Vec3d(0.5f, 0, 0.7f);
	////		var end = new Vec3d(0.5f, 0, 0.7f);
	//		var start = new Vec3d(0.45f, 1.125f, 1.08f);
	//		var end = new Vec3d(0.45f, 1.125f, 1.08f);
	//
	//		var l1 = 0.5625f;
	//		var l2 = 0.5625f;
	//
	//		var result = TwoJointIk.forwardEvaluate(entity, start, false, entity.getYaw(), l1, l2);
	//
	//		//		v.setColor(0x8000FF00);
	//		//
	//		//		v.line(start, result.kneePos());d
	//		//		v.line(result.kneePos(), result.footPos());
	//
	//		v.setColor(0xFF000000);
	//
	//		matrices.push();
	//		matrices.translate(start.x, start.y, start.z);
	//		matrices.multiply(new Quaternion(Vec3f.POSITIVE_Y, (float)result.hipYaw(), true));
	//		matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, -(float)result.hipPitch(), true));
	//		v.setMatrices(matrices.peek());
	//
	//		v.line(new Vec3d(0, 0, 0), new Vec3d(0, -l1, 0));
	//
	//		matrices.translate(0, -l1, 0);
	//		matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, (float)result.kneePitch(), true));
	//		v.setMatrices(matrices.peek());
	//
	//		v.line(new Vec3d(0, 0, 0), new Vec3d(0, -l2, 0));
	//
	//		matrices.pop();
	//
	//		matrices.push();
	//
	//		matrices.scale(-1.0F, -1.0F, 1.0F);
	//		matrices.translate(0.0D, -1.5010000467300415D, 0.0D);
	//		matrices.multiply(QuatUtil.ROT_Y_180);
	//
	//		this.model.setAngles(entity, 0, 0, 0, 0, 0);
	//		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(Resources.id("textures/entity/mammal/bantha.png")));
	//		this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
	//
	//		matrices.pop();
	//	}
}
