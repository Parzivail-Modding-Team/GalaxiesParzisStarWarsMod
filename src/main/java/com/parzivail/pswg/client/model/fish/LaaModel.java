package com.parzivail.pswg.client.model.fish;

import com.parzivail.util.client.ModelPartUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Laa - Coda
 * Created using Tabula 8.0.0
 */
@Environment(EnvType.CLIENT)
public class LaaModel<T extends FishEntity> extends EntityModel<T>
{
	public ModelPart body;
	public ModelPart head;
	public ModelPart pectoralFinLeft;
	public ModelPart analFin;
	public ModelPart tail;
	public ModelPart flailTop;
	public ModelPart flailBottom;
	public ModelPart pectoralFinRight;
	public ModelPart mouth;
	public ModelPart eyeRight;
	public ModelPart eyeLeft;
	public ModelPart tail_1;

	public LaaModel()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.flailBottom = new ModelPart(this, 0, 9);
		this.flailBottom.setPivot(0.0F, 1.0F, -2.5F);
		this.flailBottom.addCuboid(0.0F, 0.0F, -1.0F, 0.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(flailBottom, -0.3490658503988659F, 0.0F, 0.0F);
		this.mouth = new ModelPart(this, 15, 6);
		this.mouth.setPivot(0.0F, 1.0F, -1.0F);
		this.mouth.addCuboid(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
		this.eyeLeft = new ModelPart(this, 0, 0);
		this.eyeLeft.setPivot(0.8F, -0.5F, -1.9F);
		this.eyeLeft.addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeLeft, -0.17453292519943295F, -0.6981317007977318F, 0.0F);
		this.pectoralFinRight = new ModelPart(this, 0, 18);
		this.pectoralFinRight.setPivot(-1.5F, -0.5F, -1.5F);
		this.pectoralFinRight.addCuboid(0.0F, -0.5F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(pectoralFinRight, 0.2617993877991494F, -0.3490658503988659F, 0.0F);
		this.tail_1 = new ModelPart(this, 0, 3);
		this.tail_1.setPivot(0.0F, 0.0F, 3.0F);
		this.tail_1.addCuboid(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, 0.0F, 0.0F);
		this.head = new ModelPart(this, 12, 11);
		this.head.setPivot(0.0F, -0.5F, -2.0F);
		this.head.addCuboid(-1.0F, -1.5F, -2.0F, 2.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
		this.analFin = new ModelPart(this, 0, 1);
		this.analFin.setPivot(0.0F, 0.5F, 2.0F);
		this.analFin.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(analFin, -0.2617993877991494F, 0.0F, 0.0F);
		this.tail = new ModelPart(this, 14, 0);
		this.tail.setPivot(0.0F, -1.0F, 2.0F);
		this.tail.addCuboid(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.flailTop = new ModelPart(this, 0, 13);
		this.flailTop.setPivot(0.0F, -3.0F, -1.0F);
		this.flailTop.addCuboid(0.0F, -3.0F, -0.5F, 0.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
		this.body = new ModelPart(this, 0, 0);
		this.body.setPivot(0.0F, 22.0F, 0.0F);
		this.body.addCuboid(-1.5F, -3.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.pectoralFinLeft = new ModelPart(this, 0, 18);
		this.pectoralFinLeft.setPivot(1.5F, -0.5F, -1.5F);
		this.pectoralFinLeft.addCuboid(0.0F, -0.5F, 0.0F, 0.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(pectoralFinLeft, 0.2617993877991494F, 0.3490658503988659F, 0.0F);
		this.eyeRight = new ModelPart(this, 0, 0);
		this.eyeRight.mirror = true;
		this.eyeRight.setPivot(-0.8F, -0.5F, -1.9F);
		this.eyeRight.addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeRight, -0.17453292519943295F, 0.6981317007977318F, 0.0F);
		this.body.addChild(this.flailBottom);
		this.head.addChild(this.mouth);
		this.head.addChild(this.eyeLeft);
		this.body.addChild(this.pectoralFinRight);
		this.tail.addChild(this.tail_1);
		this.body.addChild(this.head);
		this.body.addChild(this.analFin);
		this.body.addChild(this.tail);
		this.body.addChild(this.flailTop);
		this.body.addChild(this.pectoralFinLeft);
		this.head.addChild(this.eyeRight);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		float f = 1.0F;
		if (!entity.isTouchingWater())
		{
			f = 1.5F;
		}

		float tailProgress = MathHelper.sin(0.6F * animationProgress);
		this.tail_1.yaw = -f * 0.05F * tailProgress;
		this.tail.yaw = -f * 0.3F * tailProgress;

		float finProgress = MathHelper.sin(0.1F * animationProgress);
		this.pectoralFinLeft.yaw = -1f - f * 0.15F * finProgress;
		this.pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;

		this.flailTop.pitch = f * 0.05F * finProgress;
		this.flailBottom.pitch = -0.3490658503988659F - f * 0.05F * finProgress;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
	{
		this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
