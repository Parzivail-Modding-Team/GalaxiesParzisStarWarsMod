package com.parzivail.util.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class EmptyBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>
{
	public EmptyBlockEntityRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	@Override
	public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
	}
}
