package com.parzivail.util.item;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface ICustomPoseItem
{
	HashMap<Item, ICustomPoseItem> ITEM_POSE_MAP = new HashMap<>();

	static void register(Item item, ICustomPoseItem pose)
	{
		ITEM_POSE_MAP.put(item, pose);
	}

	void modifyPose(ItemStack stack, ModelPart head, ModelPart rightArm, ModelPart leftArm, LivingEntity livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float tickDelta);
}
