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
 * Faa - Coda
 * Created using Tabula 8.0.0
 */
@Environment(EnvType.CLIENT)
public class FaaModel<T extends FishEntity> extends EntityModel<T>
{
	public ModelPart body;
	public ModelPart hornRight;
	public ModelPart hornLeft;
	public ModelPart tailBase;
	public ModelPart analFin;
	public ModelPart pectoralFinRight;
	public ModelPart pectoralFinLeft;
	public ModelPart crest;
	public ModelPart eyeLeft;
	public ModelPart eyeRight;
	public ModelPart tail;
	public ModelPart caudalFin;

	public FaaModel()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.body = new ModelPart(this, 0, 0);
		this.body.setPivot(0.0F, 21.0F, 0.0F);
		this.body.addCuboid(-1.5F, -3.0F, -2.5F, 3.0F, 6.0F, 5.0F, 0.0F, 0.0F, 0.0F);
		this.pectoralFinLeft = new ModelPart(this, 17, 0);
		this.pectoralFinLeft.setPivot(1.5F, 1.5F, -1.0F);
		this.pectoralFinLeft.addCuboid(0.0F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		this.hornLeft = new ModelPart(this, 16, 3);
		this.hornLeft.setPivot(0.5F, -2.0F, -2.5F);
		this.hornLeft.addCuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(hornLeft, -0.3490658503988659F, -0.3490658503988659F, 0.0F);
		this.analFin = new ModelPart(this, 0, -1);
		this.analFin.setPivot(0.0F, 1.5F, 2.5F);
		this.analFin.addCuboid(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(analFin, -0.2617993877991494F, 0.0F, 0.0F);
		this.eyeLeft = new ModelPart(this, 0, 2);
		this.eyeLeft.setPivot(-1.3F, -1.5F, -2.2F);
		this.eyeLeft.addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeLeft, -0.3490658503988659F, 0.4363323129985824F, -0.2617993877991494F);
		this.hornRight = new ModelPart(this, 16, 3);
		this.hornRight.setPivot(-0.5F, -2.0F, -2.5F);
		this.hornRight.addCuboid(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(hornRight, -0.3490658503988659F, 0.3490658503988659F, 0.0F);
		this.crest = new ModelPart(this, 14, 7);
		this.crest.setPivot(0.0F, -3.0F, -0.5F);
		this.crest.addCuboid(-0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.eyeRight = new ModelPart(this, 0, 2);
		this.eyeRight.setPivot(1.3F, -1.5F, -2.2F);
		this.eyeRight.addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(eyeRight, -0.3490658503988659F, -0.4363323129985824F, 0.2617993877991494F);
		this.tailBase = new ModelPart(this, 11, 0);
		this.tailBase.setPivot(0.0F, -0.5F, 2.5F);
		this.tailBase.addCuboid(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
		this.tail = new ModelPart(this, 0, 11);
		this.tail.setPivot(0.0F, 0.0F, 0.5F);
		this.tail.addCuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(tail, -0.17453292519943295F, 0.0F, 0.0F);
		this.caudalFin = new ModelPart(this, 6, 7);
		this.caudalFin.mirror = true;
		this.caudalFin.setPivot(0.0F, 0.0F, 3.5F);
		this.caudalFin.addCuboid(0.0F, -1.5F, -0.5F, 0.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.0F);
		this.pectoralFinRight = new ModelPart(this, 17, 0);
		this.pectoralFinRight.mirror = true;
		this.pectoralFinRight.setPivot(-1.5F, 1.5F, -1.0F);
		this.pectoralFinRight.addCuboid(-3.0F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		ModelPartUtil.setRotateAngle(pectoralFinRight, -0.03909537541112055F, 0.0F, 0.0F);
		this.body.addChild(this.pectoralFinLeft);
		this.body.addChild(this.hornLeft);
		this.body.addChild(this.analFin);
		this.body.addChild(this.eyeLeft);
		this.body.addChild(this.hornRight);
		this.body.addChild(this.crest);
		this.body.addChild(this.eyeRight);
		this.body.addChild(this.tailBase);
		this.tailBase.addChild(this.tail);
		this.tail.addChild(this.caudalFin);
		this.body.addChild(this.pectoralFinRight);
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
		this.tailBase.yaw = -f * 0.15F * tailProgress;
		this.tail.yaw = -f * 0.15F * tailProgress;
		this.caudalFin.yaw = -f * 0.25F * tailProgress;

		float finProgress = MathHelper.sin(0.1F * animationProgress);
		this.pectoralFinLeft.yaw = -1f -f * 0.15F * finProgress;
		this.pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
	{
		this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}
