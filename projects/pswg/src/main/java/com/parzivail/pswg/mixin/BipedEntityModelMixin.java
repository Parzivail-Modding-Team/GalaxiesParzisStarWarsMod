package com.parzivail.pswg.mixin;

import com.parzivail.pswg.Client;
import com.parzivail.util.client.render.ICustomPoseItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
@Environment(EnvType.CLIENT)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead
{
	@Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"))
	public void setAnglesMixin(T livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info)
	{
		if (livingEntity.isSwimming())
			return;

		for (var hand : Hand.values())
		{
			var stack = livingEntity.getStackInHand(hand);
			if (!stack.isEmpty())
			{
				@Nullable
				final ICustomPoseItem pose = ICustomPoseItem.REGISTRY.get(stack.getItem().getClass());
				if (pose != null)
					pose.modifyPose(livingEntity, hand, stack, (BipedEntityModel<T>)(Object)this, limbAngle, limbDistance, animationProgress, headYaw, headPitch, Client.getTickDelta());
			}
		}
	}
}
