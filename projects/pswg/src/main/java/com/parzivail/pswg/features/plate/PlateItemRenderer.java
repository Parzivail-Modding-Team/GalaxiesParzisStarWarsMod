package com.parzivail.pswg.features.plate;

import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class PlateItemRenderer implements ICustomItemRenderer, ICustomPoseItem
{
	public static final PlateItemRenderer INSTANCE = new PlateItemRenderer();
	@Override
	public void render(LivingEntity player, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();
		switch (renderMode)
		{
			case NONE:
			case GROUND:
				matrices.scale(0.5f, 0.5f, 0.5f);
			case HEAD:
			case FIRST_PERSON_LEFT_HAND:
			case FIRST_PERSON_RIGHT_HAND:
				matrices.translate(0.4, -0.1, 0);
				break;
			case THIRD_PERSON_LEFT_HAND:
			case THIRD_PERSON_RIGHT_HAND:
				matrices.translate(0, 0, 0);
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / 2)));
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / 6)));
				break;
			case FIXED:
				matrices.multiply(new Quaternionf().rotationZ((float)(Math.PI / 4)));
				matrices.multiply(new Quaternionf().rotationY((float)(135 * Math.PI / 180)));
				MathUtil.scalePos(matrices, 2f, 2f, 2f);
				break;
			case GUI:
				matrices.translate(0, -0.15, 0.1);
				matrices.multiply(new Quaternionf().rotationX((float)(Math.PI / 4)));
				matrices.multiply(new Quaternionf().rotationY((float)(Math.PI / -4)));
				matrices.scale(0.5f, 0.5f, 0.5f);
				MathUtil.scalePos(matrices, 2.5f, 2.5f, 2.5f);
				break;
		}
		var plateModel = P3dManager.INSTANCE.get(PlateUtil.MODEL);
		matrices.scale(0.75f, 0.75f, 0.75f);
		matrices.scale((1f / 1.6f), (1f / 1.6f), (1f / 1.6f));
		plateModel.render(matrices, vertexConsumers, stack.getNbt(), null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(PlateUtil.TEXTURE)), light, 0, 255, 255, 255, 255);
		matrices.scale(1.6f, 1.6f, 1.6f);
		List<ItemStack> foodList = new ArrayList<>();
		var nbt = stack.getNbt();
		if (nbt != null)
			for (int i = 0; i < nbt.getInt("food_amount"); i++)
				foodList.add(i, ItemStack.fromNbt(nbt.getCompound("food" + i)));
		PlateUtil.renderPlate(foodList, matrices, vertexConsumers, light, true);

		matrices.pop();
	}

	@Override
	public void modifyPose(LivingEntity entity, Hand hand, ItemStack stack, BipedEntityModel<? extends LivingEntity> model, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta)
	{
		if (entity.getStackInHand(Hand.MAIN_HAND).isOf(SwgItems.Plate.PlateItem) && entity.getStackInHand(Hand.OFF_HAND).isOf(SwgItems.Plate.PlateItem))
			poseDual(model, limbAngle, limbDistance);
		else if (entity.getStackInHand(Hand.MAIN_HAND).isOf(SwgItems.Plate.PlateItem))
			poseRight(model, limbAngle, limbDistance);
		else if (entity.getStackInHand(Hand.OFF_HAND).isOf(SwgItems.Plate.PlateItem))
			poseLeft(model, limbAngle, limbDistance);
	}

	private void poseLeft(BipedEntityModel<? extends LivingEntity> model, float limbAngle, float limbDistance)
	{
		var limbBounce = limbDistance * MathHelper.sin(limbAngle / 2f) * 0.05f;
		model.leftArm.pitch = -1.35F + model.head.pitch + limbBounce;
		model.leftArm.yaw = 0.1F + model.head.yaw;
	}

	private void poseRight(BipedEntityModel<? extends LivingEntity> model, float limbAngle, float limbDistance)
	{
		var limbBounce = limbDistance * MathHelper.sin(limbAngle / 2f) * 0.05f;
		model.rightArm.pitch = -1.35F + model.head.pitch + limbBounce;
		model.rightArm.yaw = 0.1F + model.head.yaw;
	}

	private void poseDual(BipedEntityModel<? extends LivingEntity> model, float limbAngle, float limbDistance)
	{
		var limbBounce = limbDistance * MathHelper.sin(limbAngle / 2f) * 0.05f;
		model.rightArm.pitch = -1.35F + model.head.pitch + limbBounce;
		model.rightArm.yaw = 0.1F + model.head.yaw;
		model.leftArm.pitch = -1.35F + model.head.pitch + limbBounce;
		model.leftArm.yaw = 0.1F + model.head.yaw;
	}
}
