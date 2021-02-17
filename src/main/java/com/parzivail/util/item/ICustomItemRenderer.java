package com.parzivail.util.item;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public interface ICustomItemRenderer
{
	HashMap<Item, ICustomItemRenderer> ITEM_RENDERER_MAP = new HashMap<>();

	static void register(Item item, ICustomItemRenderer renderer)
	{
		ITEM_RENDERER_MAP.put(item, renderer);
	}

	void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);
}
