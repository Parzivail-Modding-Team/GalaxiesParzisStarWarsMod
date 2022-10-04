package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.TatooineHomeDoorControllerBlock;
import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.util.block.rotating.WaterloggableRotatingBlock;
import com.parzivail.util.client.math.ClientMathUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.QuatUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

import java.util.HashMap;

public class TatooineHomeDoorRenderer implements BlockEntityRenderer<TatooineHomeDoorBlockEntity>
{
	public static final Identifier MODEL = Resources.id("block/tatooine_home_door");
	public static final Identifier TEXTURE_FRAME = Resources.id("textures/model/door/tatooine_home/frame.png");
	public static final Identifier TEXTURE_DOOR = Resources.id("textures/model/door/tatooine_home/door.png");
	public static final HashMap<DyeColor, Identifier> TEXTURE_DOOR_COLORS = Util.make(new HashMap<>(), (m) -> {
		for (var d : DyeColor.values())
			m.put(d, Resources.id("textures/model/door/tatooine_home/door_" + d.getName() + ".png"));
	});

	public TatooineHomeDoorRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}

	private static Matrix4f transform(TatooineHomeDoorBlockEntity target, String objectName, float tickDelta)
	{
		var m = new Matrix4f();
		m.loadIdentity();

		if (objectName.equals("door"))
		{
			var timer = target.getAnimationTime(tickDelta);

			if (target.isOpening())
				m.multiplyByTranslation(0, 0, 0.845f * Ease.outCubic(1 - timer));
			else
				m.multiplyByTranslation(0, 0, 0.845f * Ease.inCubic(timer));
		}

		m.multiply(Matrix4fUtil.SCALE_10_16THS);
		m.multiply(QuatUtil.ROT_Y_POS90);

		return m;
	}

	private static VertexConsumer provideLayer(VertexConsumerProvider vertexConsumerProvider, TatooineHomeDoorBlockEntity target, String objectName)
	{
		var texture = TEXTURE_FRAME;
		if (objectName.equals("door"))
			texture = TEXTURE_DOOR_COLORS.getOrDefault(target.getColor(), TEXTURE_DOOR);

		return vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(texture));
	}

	@Override
	public void render(TatooineHomeDoorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var world = blockEntity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(blockEntity.getPos());
		if (!(state.getBlock() instanceof TatooineHomeDoorControllerBlock))
			return;

		var model = P3dManager.INSTANCE.get(MODEL);
		if (model == null)
			return;

		var rotation = state.get(WaterloggableRotatingBlock.FACING);

		matrices.push();

		matrices.translate(0.5, 0, 0.5);
		matrices.multiply(ClientMathUtil.getRotation(rotation));

		model.render(matrices, vertexConsumers, blockEntity, TatooineHomeDoorRenderer::transform, TatooineHomeDoorRenderer::provideLayer, light, tickDelta, 255, 255, 255, 255);
		matrices.pop();
	}
}
