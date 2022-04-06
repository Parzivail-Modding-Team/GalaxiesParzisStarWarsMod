package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.block.TerrariumBlock;
import com.parzivail.pswg.blockentity.TerrariumBlockEntity;
import com.parzivail.util.client.StatelessWaterRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
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

		matrices.translate(0.5, 0, 0.5);

		var containedEntity = blockEntity.getContainedEntity();

		if (containedEntity != null)
		{
			var s = 0.5f / Math.max(containedEntity.getWidth(), containedEntity.getHeight());
			matrices.scale(s, s, s);

			if (containedEntity instanceof MobEntity mob)
			{
				mob.isAiDisabled();
				var p = blockEntity.getPos();
				mob.setBodyYaw(0);
				mob.prevBodyYaw = 0;
				mob.setHeadYaw(0);
				mob.prevHeadYaw = 0;
				mob.updatePositionAndAngles(p.getX(), p.getY(), p.getZ(), 0, 0);
			}

			var mc = MinecraftClient.getInstance();
			var renderer = mc.getEntityRenderDispatcher().getRenderer(containedEntity);
			renderer.render(containedEntity, 0, tickDelta, matrices, vertexConsumers, light);
		}

		matrices.pop();

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
	}
}
