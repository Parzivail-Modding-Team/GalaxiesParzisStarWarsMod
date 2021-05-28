package com.parzivail.pswg.client.model.fish;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FaaEntityModel<T extends Entity> extends SinglePartEntityModel<T>
{
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart hornRight;
	private final ModelPart hornLeft;
	private final ModelPart tailBase;
	private final ModelPart tail;
	private final ModelPart caudalFin;
	private final ModelPart analFin;
	private final ModelPart pectoralFinRight;
	private final ModelPart pectoralFinLeft;
	private final ModelPart crest;
	private final ModelPart eyeLeft;
	private final ModelPart eyeRight;

	public FaaEntityModel(ModelPart root)
	{
		this.root = root;

		body = root.getChild("body");
		hornRight = body.getChild("hornRight");
		hornLeft = body.getChild("hornLeft");
		tailBase = body.getChild("tailBase");
		tail = tailBase.getChild("tail");
		caudalFin = tail.getChild("caudalFin");
		analFin = body.getChild("analFin");
		pectoralFinRight = body.getChild("pectoralFinRight");
		pectoralFinLeft = body.getChild("pectoralFinLeft");
		crest = body.getChild("crest");
		eyeLeft = body.getChild("eyeLeft");
		eyeRight = body.getChild("eyeRight");
	}

	public FaaEntityModel()
	{
		this(FaaEntityModel.getTexturedModelData().createModel());
	}

	public static TexturedModelData getTexturedModelData()
	{
		var modelData = new ModelData();
		var root = modelData.getRoot();

		var bodyData = root.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -3f, -2.5f, 3, 6, 5), ModelTransform.of(0f, 21f, 0f, 0f, 0f, 0f));
		bodyData.addChild("hornRight", ModelPartBuilder.create().uv(16, 3).cuboid(-1f, -1f, -1f, 1, 1, 1), ModelTransform.of(-0.5f, -2f, -2.5f, -0.34906584f, 0.34906584f, 0f));
		bodyData.addChild("hornLeft", ModelPartBuilder.create().uv(16, 3).cuboid(0f, -1f, -1f, 1, 1, 1), ModelTransform.of(0.5f, -2f, -2.5f, -0.34906584f, -0.34906584f, 0f));
		var tailBaseData = bodyData.addChild("tailBase", ModelPartBuilder.create().uv(11, 0).cuboid(-1f, -1.5f, 0f, 2, 3, 1), ModelTransform.of(0f, -0.5f, 2.5f, 0f, 0f, 0f));
		var tailData = tailBaseData.addChild("tail", ModelPartBuilder.create().uv(0, 11).cuboid(-0.5f, -0.5f, 0f, 1, 1, 4), ModelTransform.of(0f, 0f, 0.5f, -0.17453292f, 0f, 0f));
		tailData.addChild("caudalFin", ModelPartBuilder.create().mirrored().uv(6, 7).cuboid(0f, -1.5f, -0.5f, 0, 3, 4), ModelTransform.of(0f, 0f, 3.5f, 0f, 0f, 0f));
		bodyData.addChild("analFin", ModelPartBuilder.create().uv(0, -1).cuboid(0f, 0f, 0f, 0, 1, 1), ModelTransform.of(0f, 1.5f, 2.5f, -0.2617994f, 0f, 0f));
		bodyData.addChild("pectoralFinRight", ModelPartBuilder.create().mirrored().uv(17, 0).cuboid(-3f, -1f, 0f, 3, 2, 0), ModelTransform.of(-1.5f, 1.5f, -1f, -0.039095376f, 0f, 0f));
		bodyData.addChild("pectoralFinLeft", ModelPartBuilder.create().uv(17, 0).cuboid(0f, -1f, 0f, 3, 2, 0), ModelTransform.of(1.5f, 1.5f, -1f, 0f, 0f, 0f));
		bodyData.addChild("crest", ModelPartBuilder.create().uv(14, 7).cuboid(-0.5f, -1f, -2f, 1, 1, 4), ModelTransform.of(0f, -3f, -0.5f, 0f, 0f, 0f));
		bodyData.addChild("eyeLeft", ModelPartBuilder.create().uv(0, 2).cuboid(-0.5f, -0.5f, -0.5f, 1, 1, 1), ModelTransform.of(-1.3f, -1.5f, -2.2f, -0.34906584f, 0.43633232f, -0.2617994f));
		bodyData.addChild("eyeRight", ModelPartBuilder.create().uv(0, 2).cuboid(-0.5f, -0.5f, -0.5f, 1, 1, 1), ModelTransform.of(1.3f, -1.5f, -2.2f, -0.34906584f, -0.43633232f, 0.2617994f));

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
		this.tailBase.yaw = -f * 0.15F * tailProgress;
		this.tail.yaw = -f * 0.15F * tailProgress;
		this.caudalFin.yaw = -f * 0.25F * tailProgress;

		var finProgress = MathHelper.sin(0.1F * animationProgress);
		this.pectoralFinLeft.yaw = -1f - f * 0.15F * finProgress;
		this.pectoralFinRight.yaw = 1f + f * 0.15F * finProgress;
	}
}
