package com.parzivail.pswg.client.model.amphibian;

import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import com.parzivail.util.client.ModelPartUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Worrt - Coda
 * Created using Tabula 8.0.0
 */
@Environment(EnvType.CLIENT)
public class WorrtModel<T extends Entity> extends EntityModel<T>
{
	public ModelPart body;
	public ModelPart jaw;
	public ModelPart armRight1;
	public ModelPart armLeft1;
	public ModelPart legLeft1;
	public ModelPart legRight1;
	public ModelPart spikes;
	public ModelPart spikes_1;
	public ModelPart spikes_2;
	public ModelPart head;
	public ModelPart tongue;
	public ModelPart tongueTip;
	public ModelPart armRight2;
	public ModelPart handRight;
	public ModelPart armLeft2;
	public ModelPart handLeft;
	public ModelPart legLeft2;
	public ModelPart footLeft;
	public ModelPart legRight2;
	public ModelPart footRight;
	public ModelPart eyeRight;
	public ModelPart eyeLeft;
	public ModelPart antennaLeft1;
	public ModelPart antennaRight1;
	public ModelPart antennaLeft2;
	public ModelPart antennaRight2;

	public WorrtModel()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.handRight = new ModelPart(this, 17, 26);
		this.handRight.setPivot(0.0F, 5.0F, 0.0F);
		this.handRight.addCuboid(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(handRight, 0.08726646259971647F, 0.0F, 0.0F);
		this.head = new ModelPart(this, 25, 0);
		this.head.setPivot(0.0F, -0.5F, -4.0F);
		this.head.addCuboid(-3.0F, -2.5F, -3.0F, 6.0F, 3.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		this.antennaRight1 = new ModelPart(this, 0, 0);
		this.antennaRight1.setPivot(-2.0F, -3.0F, 0.0F);
		this.antennaRight1.addCuboid(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(antennaRight1, 0.4363323129985824F, 0.6981317007977318F, 0.0F);
		this.legLeft2 = new ModelPart(this, 12, 17);
		this.legLeft2.mirror = true;
		this.legLeft2.setPivot(0.0F, -5.5F, -1.5F);
		this.legLeft2.addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(legLeft2, -0.7853981633974483F, 0.0F, 0.0F);
		this.legRight2 = new ModelPart(this, 12, 17);
		this.legRight2.setPivot(0.0F, -5.5F, -1.5F);
		this.legRight2.addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(legRight2, -0.7853981633974483F, 0.0F, 0.0F);
		this.handLeft = new ModelPart(this, 17, 26);
		this.handLeft.mirror = true;
		this.handLeft.setPivot(0.0F, 5.0F, 0.0F);
		this.handLeft.addCuboid(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(handLeft, 0.08726646259971647F, 0.0F, 0.0F);
		this.spikes_2 = new ModelPart(this, 0, 20);
		this.spikes_2.setPivot(2.0F, -4.0F, 0.0F);
		this.spikes_2.addCuboid(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(spikes_2, 0.0F, -0.08726646259971647F, 0.2617993877991494F);
		this.eyeLeft = new ModelPart(this, 34, 8);
		this.eyeLeft.setPivot(3.5F, -1.0F, -0.5F);
		this.eyeLeft.addCuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeLeft, 0.0F, -0.3490658503988659F, 0.0F);
		this.armRight1 = new ModelPart(this, 22, 17);
		this.armRight1.setPivot(-3.5F, 0.0F, -2.0F);
		this.armRight1.addCuboid(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(armRight1, -0.17453292519943295F, 0.0F, 0.17453292519943295F);
		this.tongue = new ModelPart(this, 44, 8);
		this.tongue.setPivot(0.0F, -0.5F, 0.0F);
		this.tongue.addCuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(tongue, 0.0F, 3.141592653589793F, 0.0F);
		this.tongueTip = new ModelPart(this, 46, 8);
		this.tongueTip.setPivot(0.0F, 0.0F, -1.0F);
		this.tongueTip.addCuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(tongueTip, 0.0F, 3.141592653589793F, 0.0F);
		this.armLeft1 = new ModelPart(this, 22, 17);
		this.armLeft1.setPivot(3.5F, 0.0F, -2.0F);
		this.armLeft1.addCuboid(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(armLeft1, -0.17453292519943295F, 0.0F, -0.17453292519943295F);
		this.eyeRight = new ModelPart(this, 34, 8);
		this.eyeRight.mirror = true;
		this.eyeRight.setPivot(-3.5F, -1.0F, -0.5F);
		this.eyeRight.addCuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeRight, 0.0F, 0.3490658503988659F, 0.0F);
		this.armLeft2 = new ModelPart(this, 30, 17);
		this.armLeft2.setPivot(1.0F, 5.0F, 0.0F);
		this.armLeft2.addCuboid(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(armLeft2, -1.3089969389957472F, 0.08726646259971647F, 0.0F);
		this.footRight = new ModelPart(this, 17, 26);
		this.footRight.setPivot(0.0F, 6.0F, 1.5F);
		this.footRight.addCuboid(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(footRight, -1.0821041362364843F, 0.0F, 0.0F);
		this.body = new ModelPart(this, 0, 0);
		this.body.setPivot(0.0F, 18.0F, 1.0F);
		this.body.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 9.0F, 0.0F, 0.0F, 0.0F);
		this.spikes_1 = new ModelPart(this, 0, 22);
		this.spikes_1.setPivot(-2.0F, -4.0F, 0.0F);
		this.spikes_1.addCuboid(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(spikes_1, 0.0F, 0.08726646259971647F, -0.2617993877991494F);
		this.antennaLeft1 = new ModelPart(this, 0, 0);
		this.antennaLeft1.setPivot(2.0F, -3.0F, 0.0F);
		this.antennaLeft1.addCuboid(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(antennaLeft1, 0.4363323129985824F, -0.6981317007977318F, 0.0F);
		this.spikes = new ModelPart(this, 26, 22);
		this.spikes.setPivot(0.0F, -4.0F, 0.0F);
		this.spikes.addCuboid(0.0F, -2.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, 0.0F, 0.0F);
		this.antennaLeft2 = new ModelPart(this, 0, 2);
		this.antennaLeft2.setPivot(0.0F, -3.0F, 0.5F);
		this.antennaLeft2.addCuboid(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(antennaLeft2, 0.8991936386169619F, 0.0F, 0.0F);
		this.armRight2 = new ModelPart(this, 30, 17);
		this.armRight2.setPivot(-1.0F, 5.0F, 0.0F);
		this.armRight2.addCuboid(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(armRight2, -1.3089969389957472F, -0.08726646259971647F, 0.0F);
		this.footLeft = new ModelPart(this, 17, 26);
		this.footLeft.mirror = true;
		this.footLeft.setPivot(0.0F, 6.0F, 1.5F);
		this.footLeft.addCuboid(-1.5F, -1.0F, -0.5F, 3.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(footLeft, -1.0821041362364843F, 0.0F, 0.0F);
		this.antennaRight2 = new ModelPart(this, 0, 2);
		this.antennaRight2.setPivot(0.0F, -3.0F, 0.5F);
		this.antennaRight2.addCuboid(-0.5F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(antennaRight2, 0.8991936386169619F, 0.0F, 0.0F);
		this.jaw = new ModelPart(this, 39, 2);
		this.jaw.setPivot(0.0F, 0.5F, -4.0F);
		this.jaw.addCuboid(-3.5F, -0.5F, -3.5F, 7.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.legLeft1 = new ModelPart(this, 0, 17);
		this.legLeft1.setPivot(3.5F, 4.0F, 4.0F);
		this.legLeft1.addCuboid(-1.5F, -6.0F, -2.0F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(legLeft1, 0.3490658503988659F, -0.6981317007977318F, 0.0F);
		this.legRight1 = new ModelPart(this, 0, 17);
		this.legRight1.setPivot(-3.5F, 4.0F, 4.0F);
		this.legRight1.addCuboid(-1.5F, -6.0F, -2.0F, 3.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(legRight1, 0.3490658503988659F, 0.6981317007977318F, 0.0F);
		this.armRight2.addChild(this.handRight);
		this.body.addChild(this.head);
		this.head.addChild(this.antennaRight1);
		this.legLeft1.addChild(this.legLeft2);
		this.legRight1.addChild(this.legRight2);
		this.armLeft2.addChild(this.handLeft);
		this.body.addChild(this.spikes_2);
		this.head.addChild(this.eyeLeft);
		this.body.addChild(this.armRight1);
		this.jaw.addChild(this.tongue);
		this.tongue.addChild(this.tongueTip);
		this.body.addChild(this.armLeft1);
		this.head.addChild(this.eyeRight);
		this.armLeft1.addChild(this.armLeft2);
		this.legRight2.addChild(this.footRight);
		this.body.addChild(this.spikes_1);
		this.head.addChild(this.antennaLeft1);
		this.body.addChild(this.spikes);
		this.antennaLeft1.addChild(this.antennaLeft2);
		this.armRight1.addChild(this.armRight2);
		this.legLeft2.addChild(this.footLeft);
		this.antennaRight1.addChild(this.antennaRight2);
		this.body.addChild(this.jaw);
		this.body.addChild(this.legLeft1);
		this.body.addChild(this.legRight1);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		//        float cycle = Math.abs(MathHelper.sin(0.02F * animationProgress));
		//		float t2 = (0.001F * ((float)(System.currentTimeMillis() % 10000))) % 2;

		float dT = MathHelper.fractionalPart(animationProgress);

		if (entity instanceof WorrtEntity)
		{
			WorrtEntity e = ((WorrtEntity)entity);
			byte timer = e.getAirborneTimer();

			if (timer > 0)
			{
				float t = MathHelper.clamp((timer + dT) / 5f, 0, 1);

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
				float t = MathHelper.clamp((-timer + dT) / 5f, 0, 1);

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

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
	{
		this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
