package com.parzivail.pswg.client.model.amphibian;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Model: Worrt
 * Author: Coda
 */
@Environment(EnvType.CLIENT)
public class WorrtEntityModel<T extends Entity> extends SinglePartEntityModel<T>
{
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart jaw;
	private final ModelPart tongue;
	private final ModelPart tongueTip;
	private final ModelPart armRight1;
	private final ModelPart armRight2;
	private final ModelPart handRight;
	private final ModelPart armLeft1;
	private final ModelPart armLeft2;
	private final ModelPart handLeft;
	private final ModelPart legLeft1;
	private final ModelPart legLeft2;
	private final ModelPart footLeft;
	private final ModelPart legRight1;
	private final ModelPart legRight2;
	private final ModelPart footRight;
	private final ModelPart spikes;
	private final ModelPart spikes1;
	private final ModelPart spikes2;
	private final ModelPart head;
	private final ModelPart eyeRight;
	private final ModelPart eyeLeft;
	private final ModelPart antennaLeft1;
	private final ModelPart antennaLeft2;
	private final ModelPart antennaRight1;
	private final ModelPart antennaRight2;

	public WorrtEntityModel(ModelPart root)
	{
		this.root = root;

		body = root.getChild("body");
		jaw = body.getChild("jaw");
		tongue = jaw.getChild("tongue");
		tongueTip = tongue.getChild("tongueTip");
		armRight1 = body.getChild("armRight1");
		armRight2 = armRight1.getChild("armRight2");
		handRight = armRight2.getChild("handRight");
		armLeft1 = body.getChild("armLeft1");
		armLeft2 = armLeft1.getChild("armLeft2");
		handLeft = armLeft2.getChild("handLeft");
		legLeft1 = body.getChild("legLeft1");
		legLeft2 = legLeft1.getChild("legLeft2");
		footLeft = legLeft2.getChild("footLeft");
		legRight1 = body.getChild("legRight1");
		legRight2 = legRight1.getChild("legRight2");
		footRight = legRight2.getChild("footRight");
		spikes = body.getChild("spikes");
		spikes1 = body.getChild("spikes1");
		spikes2 = body.getChild("spikes2");
		head = body.getChild("head");
		eyeRight = head.getChild("eyeRight");
		eyeLeft = head.getChild("eyeLeft");
		antennaLeft1 = head.getChild("antennaLeft1");
		antennaLeft2 = antennaLeft1.getChild("antennaLeft2");
		antennaRight1 = head.getChild("antennaRight1");
		antennaRight2 = antennaRight1.getChild("antennaRight2");
	}

	public WorrtEntityModel()
	{
		this(getTexturedModelData().createModel());
	}

	public static TexturedModelData getTexturedModelData()
	{
		var modelData = new ModelData();
		var root = modelData.getRoot();

		var bodyData = root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4f, -4f, -4f, 8, 8, 9), ModelTransform.of(0f, 18f, 1f, 0f, 0f, 0f));
		var jawData = bodyData.addChild("jaw", ModelPartBuilder.create().uv(39, 2).cuboid(-3.5f, -0.5f, -3.5f, 7, 2, 4), ModelTransform.of(0f, 0.5f, -4f, 0f, 0f, 0f));
		var tongueData = jawData.addChild("tongue", ModelPartBuilder.create().uv(44, 8).cuboid(-0.5f, -0.5f, -1f, 1, 1, 1), ModelTransform.of(0f, -0.5f, 0f, 0f, 3.1415927f, 0f));
		tongueData.addChild("tongueTip", ModelPartBuilder.create().uv(46, 8).cuboid(-1f, -1f, -2f, 2, 2, 2), ModelTransform.of(0f, 0f, -1f, 0f, 3.1415927f, 0f));
		var armRight1Data = bodyData.addChild("armRight1", ModelPartBuilder.create().uv(22, 17).cuboid(-2f, -1f, -1f, 2, 6, 2), ModelTransform.of(-3.5f, 0f, -2f, -0.17453292f, 0f, 0.17453292f));
		var armRight2Data = armRight1Data.addChild("armRight2", ModelPartBuilder.create().uv(30, 17).cuboid(-1f, -1f, -1f, 2, 6, 2), ModelTransform.of(-1f, 5f, 0f, -1.3089969f, -0.08726646f, 0f));
		armRight2Data.addChild("handRight", ModelPartBuilder.create().uv(17, 26).cuboid(-1.5f, -1f, -0.5f, 3, 4, 1), ModelTransform.of(0f, 5f, 0f, 0.08726646f, 0f, 0f));
		var armLeft1Data = bodyData.addChild("armLeft1", ModelPartBuilder.create().uv(22, 17).cuboid(0f, -1f, -1f, 2, 6, 2), ModelTransform.of(3.5f, 0f, -2f, -0.17453292f, 0f, -0.17453292f));
		var armLeft2Data = armLeft1Data.addChild("armLeft2", ModelPartBuilder.create().uv(30, 17).cuboid(-1f, -1f, -1f, 2, 6, 2), ModelTransform.of(1f, 5f, 0f, -1.3089969f, 0.08726646f, 0f));
		armLeft2Data.addChild("handLeft", ModelPartBuilder.create().mirrored().uv(17, 26).cuboid(-1.5f, -1f, -0.5f, 3, 4, 1), ModelTransform.of(0f, 5f, 0f, 0.08726646f, 0f, 0f));
		var legLeft1Data = bodyData.addChild("legLeft1", ModelPartBuilder.create().uv(0, 17).cuboid(-1.5f, -6f, -2f, 3, 6, 3), ModelTransform.of(3.5f, 4f, 4f, 0.34906584f, -0.6981317f, 0f));
		var legLeft2Data = legLeft1Data.addChild("legLeft2", ModelPartBuilder.create().mirrored().uv(12, 17).cuboid(-1f, 0f, -0.5f, 2, 6, 3), ModelTransform.of(0f, -5.5f, -1.5f, -0.7853982f, 0f, 0f));
		legLeft2Data.addChild("footLeft", ModelPartBuilder.create().mirrored().uv(17, 26).cuboid(-1.5f, -1f, -0.5f, 3, 4, 1), ModelTransform.of(0f, 6f, 1.5f, -1.0821041f, 0f, 0f));
		var legRight1Data = bodyData.addChild("legRight1", ModelPartBuilder.create().uv(0, 17).cuboid(-1.5f, -6f, -2f, 3, 6, 3), ModelTransform.of(-3.5f, 4f, 4f, 0.34906584f, 0.6981317f, 0f));
		var legRight2Data = legRight1Data.addChild("legRight2", ModelPartBuilder.create().uv(12, 17).cuboid(-1f, 0f, -0.5f, 2, 6, 3), ModelTransform.of(0f, -5.5f, -1.5f, -0.7853982f, 0f, 0f));
		legRight2Data.addChild("footRight", ModelPartBuilder.create().uv(17, 26).cuboid(-1.5f, -1f, -0.5f, 3, 4, 1), ModelTransform.of(0f, 6f, 1.5f, -1.0821041f, 0f, 0f));
		bodyData.addChild("spikes", ModelPartBuilder.create().uv(26, 22).cuboid(0f, -2f, -4f, 0, 2, 8), ModelTransform.of(0f, -4f, 0f, 0f, 0f, 0f));
		bodyData.addChild("spikes1", ModelPartBuilder.create().uv(0, 22).cuboid(0f, -2f, -4f, 0, 2, 8), ModelTransform.of(-2f, -4f, 0f, 0f, 0.08726646f, -0.2617994f));
		bodyData.addChild("spikes2", ModelPartBuilder.create().uv(0, 20).cuboid(0f, -4f, -4f, 0, 4, 8), ModelTransform.of(2f, -4f, 0f, 0f, -0.08726646f, 0.2617994f));
		var headData = bodyData.addChild("head", ModelPartBuilder.create().uv(25, 0).cuboid(-3f, -2.5f, -3f, 6, 3, 3), ModelTransform.of(0f, -0.5f, -4f, 0f, 0f, 0f));
		headData.addChild("eyeRight", ModelPartBuilder.create().mirrored().uv(34, 8).cuboid(-1.5f, -1f, -1f, 3, 2, 2), ModelTransform.of(-3.5f, -1f, -0.5f, 0f, 0.34906584f, 0f));
		headData.addChild("eyeLeft", ModelPartBuilder.create().uv(34, 8).cuboid(-1.5f, -1f, -1f, 3, 2, 2), ModelTransform.of(3.5f, -1f, -0.5f, 0f, -0.34906584f, 0f));
		var antennaLeft1Data = headData.addChild("antennaLeft1", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, -3f, -0.5f, 1, 4, 1), ModelTransform.of(2f, -3f, 0f, 0.43633232f, -0.6981317f, 0f));
		antennaLeft1Data.addChild("antennaLeft2", ModelPartBuilder.create().uv(0, 2).cuboid(-0.5f, -3f, -1f, 1, 3, 1), ModelTransform.of(0f, -3f, 0.5f, 0.89919364f, 0f, 0f));
		var antennaRight1Data = headData.addChild("antennaRight1", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, -3f, -0.5f, 1, 4, 1), ModelTransform.of(-2f, -3f, 0f, 0.43633232f, 0.6981317f, 0f));
		antennaRight1Data.addChild("antennaRight2", ModelPartBuilder.create().uv(0, 2).cuboid(-0.5f, -3f, -1f, 1, 3, 1), ModelTransform.of(0f, -3f, 0.5f, 0.89919364f, 0f, 0f));

		return TexturedModelData.of(modelData, 64, 32);
	}

	public ModelPart getPart()
	{
		return root;
	}

	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		var headYawRad = headYaw * 0.017453292F;
		var headPitchRad = headPitch * 0.017453292F;

		var leftAntenna = MathHelper.sin(0.02F * animationProgress);
		antennaLeft1.pitch = 0.4363323129985824F + leftAntenna * 0.1f;
		antennaLeft2.pitch = 0.8991936386169619F + leftAntenna * 0.05f + headPitchRad;

		var rightAntenna = MathHelper.cos(0.02F * animationProgress);
		antennaRight1.pitch = 0.4363323129985824F + rightAntenna * 0.1f;
		antennaRight2.pitch = 0.8991936386169619F + rightAntenna * 0.05f + headPitchRad;

		antennaRight1.yaw = 0.6981317007977318F + headYawRad / 6f;
		antennaLeft1.yaw = -0.6981317007977318F + headYawRad / 6f;

		eyeRight.yaw = 0.3490658503988659F + headYawRad / 4f;
		eyeLeft.yaw = -0.3490658503988659F + headYawRad / 4f;

		eyeRight.pitch = headPitchRad;
		eyeLeft.pitch = headPitchRad;

		var dT = Client.minecraft.getTickDelta();

		if (entity instanceof WorrtEntity e)
		{
			var timer = e.getAirborneTimer();

			if (timer > 0)
			{
				var t = MathHelper.clamp((timer - dT) / 5f, 0, 1);

				body.pitch = (-MathHelper.cos(t * MathUtil.fPI) + 1) / 4f;
				head.pitch = -(-MathHelper.cos(t * MathUtil.fPI) + 1) / 7f;
				jaw.pitch = -(-MathHelper.cos(t * MathUtil.fPI) + 1) / 7f;

				legLeft1.pitch = 0.3490658503988659F + t * 2.6f;
				legLeft2.pitch = -0.7853981633974483F - t * 2f;
				footLeft.pitch = -1.0821041362364843F + Ease.inCubic(t) * 1.1f;

				legRight1.pitch = 0.3490658503988659F + t * 2.6f;
				legRight2.pitch = -0.7853981633974483F - t * 2f;
				footRight.pitch = -1.0821041362364843F + Ease.inCubic(t) * 1.1f;

				armLeft1.pitch = -0.17453292519943295F - Ease.inCubic(t) * 1.2f;
				armLeft2.pitch = -1.3089969389957472F + Ease.outCubic(t) * 1.2f;
				handLeft.pitch = 0.08726646259971647F - t * 0.8f;

				armRight1.pitch = -0.17453292519943295F - Ease.inCubic(t) * 1.2f;
				armRight2.pitch = -1.3089969389957472F + Ease.outCubic(t) * 1.2f;
				handRight.pitch = 0.08726646259971647F - t * 0.8f;
			}
			else
			{
				var t = MathHelper.clamp((-timer - dT) / 5f, 0, 1);

				// landing [0, 1)
				body.pitch = 0.5f - (-MathHelper.cos(Ease.inCubic(t) * MathUtil.fPI) + 1) / 4f;
				head.pitch = -(-MathHelper.cos((1 - t) * MathUtil.fPI) + 1) / 7f;
				jaw.pitch = -(-MathHelper.cos((1 - t) * MathUtil.fPI) + 1) / 7f;

				legLeft1.pitch = 0.3490658503988659F + (1 - t) * 2.6f;
				legLeft2.pitch = -0.7853981633974483F - (1 - t) * 2f;
				footLeft.pitch = -1.0821041362364843F + Ease.inCubic(1 - t) * 1.1f;

				legRight1.pitch = 0.3490658503988659F + (1 - t) * 2.6f;
				legRight2.pitch = -0.7853981633974483F - (1 - t) * 2f;
				footRight.pitch = -1.0821041362364843F + Ease.inCubic(1 - t) * 1.1f;

				armLeft1.pitch = -1.3745329f + (t) * 1.2f;
				armLeft2.pitch = -0.10899687f - (float)Math.pow(t, 4) * 1.2f;
				handLeft.pitch = -0.71273357f + (float)Math.pow(t, 6) * 0.8f;

				armRight1.pitch = -1.3745329f + (t) * 1.2f;
				armRight2.pitch = -0.10899687f - (float)Math.pow(t, 4) * 1.2f;
				handRight.pitch = -0.71273357f + (float)Math.pow(t, 6) * 0.8f;
			}
		}
	}
}
