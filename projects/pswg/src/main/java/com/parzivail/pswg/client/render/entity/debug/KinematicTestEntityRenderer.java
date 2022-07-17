package com.parzivail.pswg.client.render.entity.debug;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.entity.debug.KinematicTestEntity;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.client.render.MutableAnimatedModel;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.math.TwoJointIk;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class KinematicTestEntityRenderer extends EntityRenderer<KinematicTestEntity>
{
	private final MutableAnimatedModel<KinematicTestEntity> model;

	public KinematicTestEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		model = NemManager.INSTANCE.getModel(Resources.id("mob/debug/bantha"), KinematicTestEntityRenderer::setAngles);
	}

	private static void setAngles(MutableAnimatedModel<KinematicTestEntity> model, KinematicTestEntity entity, float v, float v1, float v2, float v3, float v4, float tickDelta)
	{
		var body = model.getPart().getChild("body");
		var leftFrontLeg = body.getChild("leftFrontLeg");
		var leftFrontLeg2 = leftFrontLeg.getChild("leftFrontLeg2");

		var start = new Vec3d(0.45f, 1.125f, 1.08f);

		var l1 = 0.5625f;
		var l2 = 0.5625f;

		var result = TwoJointIk.forwardEvaluate(entity, start, false, entity.getYaw(), l1, l2);

		leftFrontLeg.pitch = -(float)(result.hipPitch() / 180 * Math.PI);
		leftFrontLeg2.pitch = (float)(result.kneePitch() / 180 * Math.PI);
	}

	@Override
	public Identifier getTexture(KinematicTestEntity entity)
	{
		return null;
	}

	@Override
	public void render(KinematicTestEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

		var con = vertexConsumers.getBuffer(RenderLayer.getLines());
		var v = VertexConsumerBuffer.Instance;
		v.init(con, matrices.peek(), 1, 1, 1, 1, OverlayTexture.DEFAULT_UV, light);

//		var start = new Vec3d(0.5f, 0, 0.7f);
//		var end = new Vec3d(0.5f, 0, 0.7f);
		var start = new Vec3d(0.45f, 1.125f, 1.08f);
		var end = new Vec3d(0.45f, 1.125f, 1.08f);

		var l1 = 0.5625f;
		var l2 = 0.5625f;

		var result = TwoJointIk.forwardEvaluate(entity, start, false, entity.getYaw(), l1, l2);

		//		v.setColor(0x8000FF00);
		//
		//		v.line(start, result.kneePos());d
		//		v.line(result.kneePos(), result.footPos());

		v.setColor(0xFF000000);

		matrices.push();
		matrices.translate(start.x, start.y, start.z);
		matrices.multiply(new Quaternion(Vec3f.POSITIVE_Y, (float)result.hipYaw(), true));
		matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, -(float)result.hipPitch(), true));
		v.setMatrices(matrices.peek());

		v.line(new Vec3d(0, 0, 0), new Vec3d(0, -l1, 0));

		matrices.translate(0, -l1, 0);
		matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, (float)result.kneePitch(), true));
		v.setMatrices(matrices.peek());

		v.line(new Vec3d(0, 0, 0), new Vec3d(0, -l2, 0));

		matrices.pop();

		matrices.push();

		matrices.scale(-1.0F, -1.0F, 1.0F);
		matrices.translate(0.0D, -1.5010000467300415D, 0.0D);
		matrices.multiply(QuatUtil.ROT_Y_180);

		this.model.setAngles(entity, 0, 0, 0, 0, 0);
		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(Resources.id("textures/block/debug16.png")));
		this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

		matrices.pop();
	}
}
