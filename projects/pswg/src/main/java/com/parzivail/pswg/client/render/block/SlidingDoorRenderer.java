package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.Sliding1x2DoorBlock;
import com.parzivail.pswg.blockentity.SlidingDoorBlockEntity;
import com.parzivail.pswg.client.render.p3d.IP3DBlockRenderer;
import com.parzivail.pswg.client.render.p3d.P3DBlockRenderTarget;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.client.render.p3d.P3dModel;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.client.math.ClientMathUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import com.parzivail.util.registry.Dyed;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.function.Supplier;

public class SlidingDoorRenderer implements IP3DBlockRenderer, BlockEntityRenderer<SlidingDoorBlockEntity>
{
	private static final Identifier MODEL = Resources.id("block/tatooine_home_door");
	private static final Identifier TEX_FRAME = Resources.id("textures/model/door/sliding_1x2/frame.png");
	private static final Dyed<Identifier> TEX_DOORS = new Dyed<>(color -> Resources.id("textures/model/door/sliding_1x2/door_" + color.getName() + ".png"));

	private static Matrix4f transformBlockState(P3DBlockRenderTarget target, String objectName, float tickDelta)
	{
		var m = new Matrix4f();
		m.loadIdentity();

		if (!(target instanceof P3DBlockRenderTarget.Block blockRenderTarget))
		{
			if (objectName.equals("door"))
				// Only draw the frame in items
				return null;
			return m;
		}

		if (objectName.equals("door"))
		{
			if (blockRenderTarget.getState().get(Sliding1x2DoorBlock.MOVING))
				return null;

			var state = blockRenderTarget.getState();
			if (Sliding1x2DoorBlock.getDoorColor(state).isEmpty())
				return null;

			if (state.get(Sliding1x2DoorBlock.OPEN))
				m.multiplyByTranslation(1.352f, 0, 0);
		}

		return m;
	}

	@Override
	public void renderBlock(MatrixStack matrices, QuadEmitter quadEmitter, P3DBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext renderContext, P3dModel model, Sprite baseSprite, HashMap<String, Sprite> additionalSprites)
	{
		if (target instanceof P3DBlockRenderTarget.Block b && b.getState().get(Sliding1x2DoorBlock.HALF) != DoubleBlockHalf.LOWER)
			return;

		var doorColor = DyeColor.BROWN;

		if (target instanceof P3DBlockRenderTarget.Block blockRenderTarget)
			doorColor = Sliding1x2DoorBlock.getDoorColor(blockRenderTarget.getState()).orElse(DyeColor.BROWN);

		DyeColor finalDoorColor = doorColor;
		model.renderBlock(
				matrices,
				quadEmitter,
				target,
				SlidingDoorRenderer::transformBlockState,
				(target1, objectName) -> objectName.equals("frame") ? baseSprite : additionalSprites.get("door_" + finalDoorColor.getName()),
				randomSupplier,
				renderContext
		);
	}

	private static Matrix4f transformBlockEntity(SlidingDoorBlockEntity target, String objectName, float tickDelta)
	{
		if (!objectName.equals("door"))
			return null;

		var m = new Matrix4f();
		m.loadIdentity();

		m.multiply(Matrix4fUtil.SCALE_10_16THS);

		var state = target.getOccupiedState();
		if (Sliding1x2DoorBlock.getDoorColor(state).isEmpty())
			return null;

		var timer = target.getAnimationTime(tickDelta);

		if (target.isOpening())
			m.multiplyByTranslation(0, 0, 1.352f * Ease.outCubic(1 - timer));
		else
			m.multiplyByTranslation(0, 0, 1.352f * Ease.inCubic(timer));

		m.multiply(QuatUtil.ROT_Y_POS90);

		return m;
	}

	private static VertexConsumer provideLayer(VertexConsumerProvider vertexConsumerProvider, SlidingDoorBlockEntity target, String objectName)
	{
		var texture = TEX_FRAME;
		if (objectName.equals("door"))
			texture = TEX_DOORS.get(Sliding1x2DoorBlock.getDoorColor(target.getOccupiedState()).orElse(DyeColor.BROWN));

		return vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(texture));
	}

	@Override
	public void render(SlidingDoorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var world = entity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(entity.getPos());
		if (!state.isOf(SwgBlocks.Door.Sliding1x2) || !state.get(Sliding1x2DoorBlock.MOVING) && entity.getIdleTimer() == 0)
			return;

		var model = P3dManager.INSTANCE.get(MODEL);
		if (model == null)
			return;

		var rotation = state.get(WaterloggableRotatingBlock.FACING);

		matrices.push();

		matrices.translate(0.5, 0, 0.5);
		matrices.multiply(ClientMathUtil.getRotation(rotation));

		model.render(matrices, vertexConsumers, entity, SlidingDoorRenderer::transformBlockEntity, SlidingDoorRenderer::provideLayer, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
	}
}
