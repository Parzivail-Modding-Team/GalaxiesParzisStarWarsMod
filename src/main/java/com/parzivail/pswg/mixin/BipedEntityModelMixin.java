package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.util.item.ICustomPoseItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
@Environment(EnvType.CLIENT)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead
{
	@Shadow
	public ModelPart head;

	@Shadow
	public ModelPart rightArm;

	@Shadow
	public ModelPart leftArm;

	@Inject(method = "setAngles", at = @At(value = "TAIL"))
	public void setAnglesMixin(T livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info)
	{
		if (!livingEntity.getOffHandStack().isEmpty() || livingEntity.isSwimming() || livingEntity.hasVehicle())
			return;

		ItemStack stack = livingEntity.getMainHandStack();
		if (!stack.isEmpty())
		{
			@Nullable
			final ICustomPoseItem pose = ICustomPoseItem.ITEM_POSE_MAP.get(stack.getItem());
			if (pose != null)
				pose.modifyPose(stack, head, rightArm, leftArm, livingEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, Client.minecraft.getTickDelta());
		}
	}
}
