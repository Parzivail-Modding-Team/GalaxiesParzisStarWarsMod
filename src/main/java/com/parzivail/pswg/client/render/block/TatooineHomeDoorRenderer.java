package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.blockentity.TatooineHomeDoorBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class TatooineHomeDoorRenderer extends BlockEntityRenderer<TatooineHomeDoorBlockEntity>
{
	private static final ItemStack stack = new ItemStack(SwgBlocks.Door.TatooineHome, 1);

	public TatooineHomeDoorRenderer(BlockEntityRenderDispatcher dispatcher)
	{
		super(dispatcher);
	}

	@Override
	public void render(TatooineHomeDoorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		matrices.push();

		RenderLayer layer = RenderLayer.of("stencil_mask", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("stencil_mask_gl", () ->
		{
			GL11.glEnable(GL11.GL_STENCIL_TEST);

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
		})).build(true));

		RenderLayer layer2 = RenderLayer.of("stencil_target", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, GL11.GL_QUADS, 2097152, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("stencil_target_gl", () ->
		{
			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
		}, () -> {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		})).diffuseLighting(new RenderPhase.DiffuseLighting(true)).build(true));

		matrices.push();
		matrices.translate(0.5, 1, 0.5);
		matrices.scale(1, 2, 1);
		VertexConsumerBuffer.Instance.init(vertexConsumers.getBuffer(layer), matrices.peek(), 0, 1, 0, 1, overlay, light);

		RenderShapes.box(VertexConsumerBuffer.Instance);
		matrices.pop();

		matrices.push();
		matrices.translate(0.5 + MathHelper.sin((float)(((System.currentTimeMillis() % 5000) / 5000f) * Math.PI * 2)), 1, 0.5);
		matrices.scale(1, 2, 0.5f);
		VertexConsumerBuffer.Instance.init(vertexConsumers.getBuffer(layer2), matrices.peek(), 1, 0, 0, 1, overlay, light);

		RenderShapes.box(VertexConsumerBuffer.Instance);
		matrices.pop();

		matrices.pop();
	}
}
