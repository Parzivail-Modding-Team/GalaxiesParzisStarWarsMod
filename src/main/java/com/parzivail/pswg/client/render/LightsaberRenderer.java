package com.parzivail.pswg.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LightsaberRenderer
{
	private static final RenderLayer LAYER_LIGHTSABER_CORE = RenderLayer.of("lightsaber_core", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().build(true));

	private static final RenderLayer LAYER_LIGHTSABER_STENCIL_MASK = RenderLayer.of("core_stencil_mask2", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("core_stencil_mask_gl", () ->
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

	private static final RenderLayer LAYER_LIGHTSABER_STENCIL_TARGET = RenderLayer.of("glow_stencil_target", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 2097152, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("glow_stencil_target_gl", () ->
	{
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF); // Fail test if stencil value is 1
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ZERO);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	})).build(true));

	public static void renderBlade(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean unstable, float baseLength, float lengthCoefficient, boolean cap, int coreColor, int glowColor)
	{
		VertexConsumer vc;

		var totalLength = baseLength * lengthCoefficient;
		var shake = (1.1f - lengthCoefficient) * 0.004f;

		double dX = (float)Resources.RANDOM.nextGaussian() * shake;
		double dY = (float)Resources.RANDOM.nextGaussian() * shake;
		matrices.translate(dX, 0, dY);

		vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_CORE);

		final var offset = (float)Resources.RANDOM.nextGaussian();

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderCore(totalLength, coreColor, unstable, offset, cap);

		vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_STENCIL_MASK);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderCore(totalLength, coreColor, unstable, offset, cap);

		vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_STENCIL_TARGET);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderGlow(totalLength, glowColor, unstable, cap);
	}

	private static void renderCore(float bladeLength, int coreColor, boolean unstable, float simplexOffset, boolean cap)
	{
		if (bladeLength == 0)
			return;

		final var segments = unstable ? 15 : 1;
		final var dSegments = 1f / segments;
		final var dLength = bladeLength / segments;

		final var solidThickness = 0.027f;
		final var cappedThickness = 0.02f;
		final var topThickness = cap ? cappedThickness : solidThickness;

		VertexConsumerBuffer.Instance.setColor(coreColor);

		if (cap)
		{
			var dTRoundBottom = unstable ? (float)Resources.SIMPLEX_0.noise2(simplexOffset, dLength * (segments + 1)) * 0.005f : 0;
			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, 0.01f, cappedThickness + dTRoundBottom, 0, bladeLength + 0.025f, 0, 0, bladeLength, 0);
		}

		for (var i = 0; i < segments; i++)
		{
			var topThicknessLerp = MathHelper.lerp(dSegments * (i + 1), solidThickness, topThickness);
			var bottomThicknessLerp = MathHelper.lerp(dSegments * i, solidThickness, topThickness);

			var dTTop = unstable ? (float)Resources.SIMPLEX_0.noise2(simplexOffset, dLength * (i + 1)) * 0.005f : 0;
			var dTBottom = unstable ? (float)Resources.SIMPLEX_0.noise2(simplexOffset, dLength * i) * 0.005f : 0;

			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
		}
	}

	public static void renderGlow(float bladeLength, int bladeColor, boolean unstable, boolean cap)
	{
		if (bladeLength == 0)
			return;

		var thicknessTop = cap ? 0.14f : 0.16f;

		for (var layer = 22; layer > 12; layer--)
		{
			VertexConsumerBuffer.Instance.setColor(bladeColor, (int)MathUtil.remap(layer, 12, 22, 0, 128));

			float layerThicknessModifier = 0;
			if (unstable)
				layerThicknessModifier = (float)Resources.RANDOM.nextGaussian() * 0.003f;

			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, thicknessTop - layerThicknessModifier - 0.0058f * layer, 0.16f - layerThicknessModifier - 0.0058f * layer, 0, layerThicknessModifier + bladeLength + (cap ? layer - 9 : 10 - (layer - 9)) * 0.006f, 0, 0, -(20 - layer) * 0.005f, 0);
		}
	}
}
