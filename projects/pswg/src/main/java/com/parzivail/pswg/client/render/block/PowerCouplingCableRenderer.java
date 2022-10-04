package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.blockentity.PowerCouplingBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.client.math.ClientMathUtil;
import com.parzivail.util.math.Matrix4fUtil;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class PowerCouplingCableRenderer implements BlockEntityRenderer<PowerCouplingBlockEntity>
{
	public PowerCouplingCableRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	@Override
	public void render(PowerCouplingBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var world = blockEntity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(blockEntity.getPos());
		if (!state.isOf(SwgBlocks.Power.Coupling))
			return;

		matrices.push();

		var offsetMatrices = new MatrixStack();
		var rotation = state.get(WaterloggableRotatingBlock.FACING);
		offsetMatrices.multiply(ClientMathUtil.getRotation(rotation));
		var offset = Matrix4fUtil.transform(new Vec3d(-5 / 16f, 0, 0), offsetMatrices.peek().getPositionMatrix());

		matrices.translate(0.5, 0.5, 0.5);
		matrices.translate(offset.x, offset.y, offset.z);

		var src = blockEntity.getPos();
		var srcVec3 = new Vec3d(src.getX(), src.getY(), src.getZ());
		int i = 0;
		for (var pos : blockEntity.getTargets())
		{
			var targetPos = pos.add(src);

			var targetState = world.getBlockState(targetPos);
			if (!targetState.isOf(SwgBlocks.Power.Coupling))
				continue;

			var destVec3 = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()).subtract(offset);

			var destOffsetMatrices = new MatrixStack();
			var destRotation = targetState.get(WaterloggableRotatingBlock.FACING);
			destOffsetMatrices.multiply(ClientMathUtil.getRotation(destRotation));
			var destOffset = Matrix4fUtil.transform(new Vec3d(-5 / 16f, 0, 0), destOffsetMatrices.peek().getPositionMatrix());

			renderCable(world, i, srcVec3, destVec3.add(destOffset), tickDelta, matrices, vertexConsumers);
			i++;
		}

		matrices.pop();
	}

	private void renderCable(World world, int idx, Vec3d source, Vec3d dest, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider)
	{
		matrices.push();
		var dX = (float)(dest.x - source.x);
		var dY = (float)(dest.y - source.y);
		var dZ = (float)(dest.z - source.z);
		var size = 0.04f;
		var vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
		var matrix4f = matrices.peek().getPositionMatrix();
		var n = MathHelper.fastInverseSqrt(dX * dX + dZ * dZ) * size / 2.0F;
		var o = dZ * n;
		var p = dX * n;
		var srcPos = new BlockPos(source);
		var dstPos = new BlockPos(dest);
		int srcBlockLight = world.getLightLevel(LightType.BLOCK, srcPos);
		int dstBlockLight = world.getLightLevel(LightType.BLOCK, dstPos);
		int srcSkyLight = world.getLightLevel(LightType.SKY, srcPos);
		int dstSkyLight = world.getLightLevel(LightType.SKY, dstPos);

		// insert degenerate triangle into strip
		vertexConsumer.vertex(matrix4f, 0, 0, 0).color(0).light(0).next();
		vertexConsumer.vertex(matrix4f, 0, 0, 0).color(0).light(0).next();

		for (var u = 0; u <= 24; ++u)
			renderCableSegment(world, idx, tickDelta, vertexConsumer, matrix4f, dX, dY, dZ, srcBlockLight, dstBlockLight, srcSkyLight, dstSkyLight, size, size, o, p, u, false);

		for (var u = 24; u >= 0; --u)
			renderCableSegment(world, idx, tickDelta, vertexConsumer, matrix4f, dX, dY, dZ, srcBlockLight, dstBlockLight, srcSkyLight, dstSkyLight, size, 0.0F, o, p, u, true);

		// insert degenerate triangle into strip
		vertexConsumer.vertex(matrix4f, 0, 0, 0).color(0).light(0).next();
		vertexConsumer.vertex(matrix4f, 0, 0, 0).color(0).light(0).next();

		matrices.pop();
	}

	private static void renderCableSegment(World world, int idx, float tickDelta, VertexConsumer vertexConsumer, Matrix4f positionMatrix, float dX, float dY, float dZ, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot)
	{
		var t = (float)pieceIndex / 24.0F;
		var blockLight = (int)MathHelper.lerp(t, (float)leashedEntityBlockLight, (float)holdingEntityBlockLight);
		var skyLight = (int)MathHelper.lerp(t, (float)leashedEntitySkyLight, (float)holdingEntitySkyLight);
		var packedLight = LightmapTextureManager.pack(blockLight, skyLight);

		var p0 = new Vec3d(0, 0, 0);
		var p1 = new Vec3d(dX, dY, dZ);

		var seed = (int)(dX) ^ (31L * (int)(dY + idx)) ^ (63L * (int)(dZ));

		var T = 2 + (seed % 3) / 16.0f;

		var pC = p0.add(p1).multiply(0.5).subtract(0, T, 0);

		var time = world.getTime();
		time += seed;
		time %= 100;
		var timeRads = (time + tickDelta) / 50f * MathHelper.PI;

		var left = new Vec3d(dX, dY, dZ).normalize().crossProduct(new Vec3d(0, 1, 0)).multiply(0.1);
		pC = pC.add(left.multiply(MathHelper.sin(timeRads)));

		var oneMinusT = (1 - t);
		var B = p0.multiply(oneMinusT * oneMinusT).add(pC.multiply(2 * t * oneMinusT)).add(p1.multiply(t * t));

		var x = (float)B.x;
		var y = (float)B.y;
		var z = (float)B.z;

		vertexConsumer.vertex(positionMatrix, x - k, y + j, z + l).color(0xFF202020).light(packedLight).next();
		vertexConsumer.vertex(positionMatrix, x + k, y + i - j, z - l).color(0xFF202020).light(packedLight).next();
	}
}
