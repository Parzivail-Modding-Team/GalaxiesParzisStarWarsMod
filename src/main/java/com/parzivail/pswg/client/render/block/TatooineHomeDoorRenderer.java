package com.parzivail.pswg.client.render.block;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.rigs.TatooineHomeDoorRig;
import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.client.render.pr3.PR3File;
import com.parzivail.pswg.client.render.pr3.PR3Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class TatooineHomeDoorRenderer implements BlockEntityRenderer<TatooineHomeDoorBlockEntity>
{
	private final Supplier<PR3Model<TatooineHomeDoorBlockEntity, TatooineHomeDoorRig.Part>> model;

	private static final Identifier TEXTURE_FRAME = Resources.id("textures/model/door/tatooine_home/frame.png");
	private static final Identifier TEXTURE_DOOR = Resources.id("textures/model/door/tatooine_home/door.png");

	public TatooineHomeDoorRenderer(BlockEntityRendererFactory.Context ctx)
	{
		model = Suppliers.memoize(() -> new PR3Model<>(PR3File.tryLoad(Resources.id("models/block/door/tatooine_home.pr3")), TatooineHomeDoorRig.Part.class, TatooineHomeDoorRig.INSTANCE::transform));
	}

	@Override
	public void render(TatooineHomeDoorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		matrices.push();

		var m = model.get();

		m.renderObject(vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE_FRAME)), "Frame", blockEntity, tickDelta, matrices, light);

//		m.renderObject(vertexConsumers.getBuffer(LAYER_STENCIL_MASK), "StencilMask", blockEntity, tickDelta, matrices, light);
		m.renderObject(vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE_DOOR)), "Door", blockEntity, tickDelta, matrices, light);

		matrices.pop();
	}
}
