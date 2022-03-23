package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.blockentity.PowerCouplingBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.rotating.RotatingBlock;
import com.parzivail.util.math.ClientMathUtil;
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
		var rotation = state.get(RotatingBlock.FACING);
		offsetMatrices.multiply(ClientMathUtil.getRotation(rotation));
		var offset = Matrix4fUtil.transform(new Vec3d(-5 / 16f, 0, 0), offsetMatrices.peek().getPositionMatrix());

		matrices.translate(0.5, 0.5, 0.5);
		matrices.translate(offset.x, offset.y, offset.z);

		var src = blockEntity.getPos();
		var srcVec3 = new Vec3d(src.getX(), src.getY(), src.getZ());
		for (var pos : blockEntity.getTargets())
		{
			var targetState = world.getBlockState(pos);
			if (!targetState.isOf(SwgBlocks.Power.Coupling))
				continue;

			var destVec3 = new Vec3d(pos.getX(), pos.getY(), pos.getZ()).subtract(offset);

			var destOffsetMatrices = new MatrixStack();
			var destRotation = targetState.get(RotatingBlock.FACING);
			destOffsetMatrices.multiply(ClientMathUtil.getRotation(destRotation));
			var destOffset = Matrix4fUtil.transform(new Vec3d(-5 / 16f, 0, 0), destOffsetMatrices.peek().getPositionMatrix());

			renderCable(world, srcVec3, destVec3.add(destOffset), tickDelta, matrices, vertexConsumers);
		}

		matrices.pop();
	}

	private void renderCable(World world, Vec3d source, Vec3d dest, float tickDelta, MatrixStack matrices, VertexConsumerProvider provider)
	{
		matrices.push();
		var j = (float)(dest.x - source.x);
		var k = (float)(dest.y - source.y);
		var l = (float)(dest.z - source.z);
		var m = 0.05F;
		var vertexConsumer = provider.getBuffer(RenderLayer.getLeash());
		var matrix4f = matrices.peek().getPositionMatrix();
		var n = MathHelper.fastInverseSqrt(j * j + l * l) * m / 2.0F;
		var o = l * n;
		var p = j * n;
		var blockPos = new BlockPos(source);
		var blockPos2 = new BlockPos(dest);
		int q = world.getLightLevel(LightType.BLOCK, blockPos);
		int r = world.getLightLevel(LightType.BLOCK, blockPos2);
		int s = world.getLightLevel(LightType.SKY, blockPos);
		int t = world.getLightLevel(LightType.SKY, blockPos2);

		for (var u = 0; u <= 12; ++u)
		{
			renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, m, m, o, p, u, false);
		}

		for (var u = 12; u >= 0; --u)
		{
			renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, m, 0.0F, o, p, u, true);
		}

		matrices.pop();
	}

	private static void renderLeashPiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g, float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight, int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot)
	{
		var m = (float)pieceIndex / 12.0F;
		var n = (int)MathHelper.lerp(m, (float)leashedEntityBlockLight, (float)holdingEntityBlockLight);
		var o = (int)MathHelper.lerp(m, (float)leashedEntitySkyLight, (float)holdingEntitySkyLight);
		var p = LightmapTextureManager.pack(n, o);
		var u = f * m;

		var distance = MathHelper.sqrt(f * f + g * g + h * h);
		var lateralDistance = MathHelper.sqrt(f * f + h * h);
		var sag = 0.6f;
		var v = MathHelper.lerp((float)Math.pow(m, sag * distance / lateralDistance), -distance * m, g);
		var w = h * m;
		vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(0xFF202020).light(p).next();
		vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(0xFF202020).light(p).next();
	}
}
