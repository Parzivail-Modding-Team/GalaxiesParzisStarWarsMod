package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.block.TerrariumBlock;
import com.parzivail.pswg.blockentity.TerrariumBlockEntity;
import com.parzivail.util.client.StatelessWaterRenderer;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Vec3d;

public class TerrariumRenderer implements BlockEntityRenderer<TerrariumBlockEntity>
{
	public TerrariumRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	@Override
	public void render(TerrariumBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var world = blockEntity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(blockEntity.getPos());
		if (!(state.getBlock() instanceof TerrariumBlock))
			return;

		matrices.push();

		var waterState = state.get(TerrariumBlock.WATER_LEVEL);
		if (waterState > 0)
		{
			var waterLevel = waterState / 9.0f;

			var fs = Fluids.WATER.getStill(false);
			var wr = StatelessWaterRenderer.INSTANCE;

			var waterColor = BiomeColors.getWaterColor(world, blockEntity.getPos());

			wr.render(world, waterColor, vertexConsumers.getBuffer(RenderLayers.getFluidLayer(fs)), matrices.peek().getPositionMatrix(), false,
			          true, true, true, true, true, true, true,
			          waterLevel, waterLevel, waterLevel, waterLevel,
			          Vec3d.ZERO, light, light);
		}

		matrices.pop();
	}
}
