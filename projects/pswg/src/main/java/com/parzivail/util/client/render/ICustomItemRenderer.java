package com.parzivail.util.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface ICustomItemRenderer
{
	HashMap<Class<? extends Item>, ICustomItemRenderer> REGISTRY = new HashMap<>();

	static void register(Class<? extends Item> item, ICustomItemRenderer renderer)
	{
		REGISTRY.put(item, renderer);
	}

	void render(LivingEntity player, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);
}
