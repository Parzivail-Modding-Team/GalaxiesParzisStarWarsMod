package com.parzivail.util.client.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface ICustomPoseItem
{
	HashMap<Class<? extends Item>, ICustomPoseItem> REGISTRY = new HashMap<>();

	static void register(Class<? extends Item> item, ICustomPoseItem pose)
	{
		REGISTRY.put(item, pose);
	}

	void modifyPose(LivingEntity entity, ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta);
}
