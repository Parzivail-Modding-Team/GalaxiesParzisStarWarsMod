package com.parzivail.pswg.client.render.entity.amphibian;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WorrtEntityRenderer extends MobEntityRenderer<WorrtEntity, SinglePartEntityModel<WorrtEntity>>
{
	public WorrtEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, Client.ResourceManagers.getNemManager().getModel(Resources.id("mob/amphibian/worrt"), WorrtEntityRenderer::setAngles), 0.5f);
	}

	@Override
	public Identifier getTexture(WorrtEntity entity)
	{
		return Resources.id("textures/entity/amphibian/worrt.png");
	}

	public static void setAngles(SinglePartEntityModel<WorrtEntity> model, WorrtEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		var minecraft = MinecraftClient.getInstance();

		var timer = entity.getAirborneLerp();

		if (entity.isAiDisabled())
		{
			timer = 0;
			animationProgress = 0;
		}

		var body = model.getPart().getChild("body");
		var jaw = body.getChild("jaw");
		var armRight1 = body.getChild("armRight1");
		var armRight2 = armRight1.getChild("armRight2");
		var handRight = armRight2.getChild("handRight");
		var armLeft1 = body.getChild("armLeft1");
		var armLeft2 = armLeft1.getChild("armLeft2");
		var handLeft = armLeft2.getChild("handLeft");
		var legLeft1 = body.getChild("legLeft1");
		var legLeft2 = legLeft1.getChild("legLeft2");
		var footLeft = legLeft2.getChild("footLeft");
		var legRight1 = body.getChild("legRight1");
		var legRight2 = legRight1.getChild("legRight2");
		var footRight = legRight2.getChild("footRight");
		var head = body.getChild("head");
		var eyeRight = head.getChild("eyeRight");
		var eyeLeft = head.getChild("eyeLeft");
		var antennaLeft1 = head.getChild("antennaLeft1");
		var antennaLeft2 = antennaLeft1.getChild("antennaLeft2");
		var antennaRight1 = head.getChild("antennaRight1");
		var antennaRight2 = antennaRight1.getChild("antennaRight2");

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

		if (entity.getVelocity().y > 0)
		{
			var t = MathHelper.clamp(timer, 0, 1);

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
			var t = MathHelper.clamp(1-timer, 0, 1);

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
