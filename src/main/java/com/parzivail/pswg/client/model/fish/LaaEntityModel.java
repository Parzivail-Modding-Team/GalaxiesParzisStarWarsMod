package com.parzivail.pswg.client.model.fish;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Model: Laa
 * Author: Coda
 */
@Environment(EnvType.CLIENT)
public class LaaEntityModel<T extends Entity> extends SinglePartEntityModel<T>
{
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart eyeRight;
	private final ModelPart eyeLeft;
	private final ModelPart pectoralFinLeft;
	private final ModelPart analFin;
	private final ModelPart tail;
	private final ModelPart tail1;
	private final ModelPart flailTop;
	private final ModelPart flailBottom;
	private final ModelPart pectoralFinRight;

	public LaaEntityModel(ModelPart root)
	{
		this.root = root;

		body = root.getChild("body");
		head = body.getChild("head");
		mouth = head.getChild("mouth");
		eyeRight = head.getChild("eyeRight");
		eyeLeft = head.getChild("eyeLeft");
		pectoralFinLeft = body.getChild("pectoralFinLeft");
		analFin = body.getChild("analFin");
		tail = body.getChild("tail");
		tail1 = tail.getChild("tail1");
		flailTop = body.getChild("flailTop");
		flailBottom = body.getChild("flailBottom");
		pectoralFinRight = body.getChild("pectoralFinLeft1");
	}

	public LaaEntityModel()
	{
		this(LaaEntityModel.getTexturedModelData().createModel());
	}

	public static TexturedModelData getTexturedModelData()
	{
		var modelData = new ModelData();
		var root = modelData.getRoot();

		var bodyData = root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -3f, -2f, 3, 5, 4), ModelTransform.of(0f, 22f, 0f, 0f, 0f, 0f));
		var headData = bodyData.addChild("head", ModelPartBuilder.create().uv(12, 11).cuboid(-1f, -1.5f, -2f, 2, 3, 2), ModelTransform.of(0f, -0.5f, -2f, 0f, 0f, 0f));
		headData.addChild("mouth", ModelPartBuilder.create().uv(15, 6).cuboid(-0.5f, -1f, -3f, 1, 2, 3), ModelTransform.of(0f, 1f, -1f, 0f, 0f, 0f));
		headData.addChild("eyeRight", ModelPartBuilder.create().mirrored().uv(0, 0).cuboid(-0.5f, -0.5f, -0.5f, 1, 1, 1), ModelTransform.of(-0.8f, -0.5f, -1.9f, -0.17453292f, 0.6981317f, 0f));
		headData.addChild("eyeLeft", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, -0.5f, -0.5f, 1, 1, 1), ModelTransform.of(0.8f, -0.5f, -1.9f, -0.17453292f, -0.6981317f, 0f));
		bodyData.addChild("pectoralFinLeft", ModelPartBuilder.create().uv(0, 18).cuboid(0f, -0.5f, 0f, 0, 2, 5), ModelTransform.of(1.5f, -0.5f, -1.5f, 0.2617994f, 0.34906584f, 0f));
		bodyData.addChild("analFin", ModelPartBuilder.create().uv(0, 1).cuboid(0f, 0f, 0f, 0, 1, 1), ModelTransform.of(0f, 0.5f, 2f, -0.2617994f, 0f, 0f));
		var tailData = bodyData.addChild("tail", ModelPartBuilder.create().uv(14, 0).cuboid(-0.5f, -1f, 0f, 1, 2, 4), ModelTransform.of(0f, -1f, 2f, 0f, 0f, 0f));
		tailData.addChild("tail1", ModelPartBuilder.create().uv(0, 3).cuboid(0f, -2.5f, 0f, 0, 5, 6), ModelTransform.of(0f, 0f, 3f, 0f, 0f, 0f));
		bodyData.addChild("flailTop", ModelPartBuilder.create().uv(0, 13).cuboid(0f, -3f, -0.5f, 0, 3, 6), ModelTransform.of(0f, -3f, -1f, 0f, 0f, 0f));
		bodyData.addChild("flailBottom", ModelPartBuilder.create().uv(0, 9).cuboid(0f, 0f, -1f, 0, 3, 6), ModelTransform.of(0f, 1f, -2.5f, -0.34906584f, 0f, 0f));
		bodyData.addChild("pectoralFinLeft1", ModelPartBuilder.create().uv(0, 18).cuboid(0f, -0.5f, 0f, 0, 2, 5), ModelTransform.of(-1.5f, -0.5f, -1.5f, 0.2617994f, -0.34906584f, 0f));

		return TexturedModelData.of(modelData, 64, 32);
	}

	public ModelPart getPart()
	{
		return root;
	}

	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{
		var f = 1.0F;
		if (!entity.isTouchingWater())
		{
			f = 1.5F;
		}

		var tailProgress = MathHelper.sin(0.6F * animationProgress);
		this.tail1.yaw = -f * 0.05F * tailProgress;
		this.tail.yaw = -f * 0.3F * tailProgress;

		var finProgress = MathHelper.sin(0.1F * animationProgress);
		this.pectoralFinLeft.yaw = -1f - f * 0.15F * finProgress;
		this.pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;

		this.flailTop.pitch = f * 0.05F * finProgress;
		this.flailBottom.pitch = -0.3490658503988659F - f * 0.05F * finProgress;
	}
}
