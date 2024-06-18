package com.parzivail.pswg.features.plate;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class PlateBlockRenderer implements BlockEntityRenderer<PlateBlockEntity>
{
	public PlateBlockRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}
	@Override
	public void render(PlateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		if(entity.getWorld()!=null&&entity.getWorld().isClient){
			PlateUtil.renderPlate(entity.FOODS, matrices, vertexConsumers, light, false);
		}
	}
}
