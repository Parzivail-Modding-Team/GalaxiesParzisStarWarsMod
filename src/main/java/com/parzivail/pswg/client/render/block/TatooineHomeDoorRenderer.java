package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.block.rigs.RigTatooineHomeDoor;
import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.client.pr3.PR3File;
import com.parzivail.pswg.client.pr3.PR3Model;
import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.lwjgl.opengl.GL11;

public class TatooineHomeDoorRenderer extends BlockEntityRenderer<TatooineHomeDoorBlockEntity>
{
	private final Lazy<PR3Model<TatooineHomeDoorBlockEntity, RigTatooineHomeDoor.Part>> model;
	private static final ItemStack stack = new ItemStack(SwgBlocks.Door.TatooineHomeController, 1);

	private static final Identifier TEXTURE_FRAME = Resources.identifier("textures/model/door/tatooine_home/frame.png");
	private static final Identifier TEXTURE_DOOR = Resources.identifier("textures/model/door/tatooine_home/door.png");

	private static final RenderLayer LAYER_STENCIL_MASK = RenderLayer.of("thdoor_stencil_mask", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("stencil_mask_gl", () ->
	{
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);

		GL11.glPolygonOffset(0, -3);

		GL11.glStencilMask(0xFF); // Write to stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

		GL11.glColorMask(false, false, false, false);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GL11.glDepthMask(false);
	}, () -> {
		GL11.glDepthMask(true);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glColorMask(true, true, true, true);
		GL11.glStencilMask(0x00); // Don't write anything to stencil buffer
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
	})).cull(new RenderPhase.Cull(false)).build(true));

	private static final RenderLayer LAYER_STENCIL_TARGET = RenderLayer.of("thdoor_stencil_target", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, GL11.GL_QUADS, 2097152, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("stencil_target_gl", () ->
	{
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
	}, () -> {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	})).diffuseLighting(new RenderPhase.DiffuseLighting(true)).texture(new RenderPhase.Texture(TEXTURE_DOOR, false, true)).build(true));

	public TatooineHomeDoorRenderer(BlockEntityRenderDispatcher dispatcher)
	{
		super(dispatcher);
		model = new Lazy<>(() -> new PR3Model<>(PR3File.tryLoad(Resources.identifier("models/block/door/tatooine_home.pr3")), RigTatooineHomeDoor.Part.class, RigTatooineHomeDoor.INSTANCE::transform));
	}

	@Override
	public void render(TatooineHomeDoorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		matrices.push();

		PR3Model<TatooineHomeDoorBlockEntity, RigTatooineHomeDoor.Part> m = model.get();

		m.renderObject(vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE_FRAME)), "Frame", blockEntity, tickDelta, matrices, light);

		m.renderObject(vertexConsumers.getBuffer(LAYER_STENCIL_MASK), "StencilMask", blockEntity, tickDelta, matrices, light);
		m.renderObject(vertexConsumers.getBuffer(LAYER_STENCIL_TARGET), "Door", blockEntity, tickDelta, matrices, light);

		matrices.pop();
	}
}
