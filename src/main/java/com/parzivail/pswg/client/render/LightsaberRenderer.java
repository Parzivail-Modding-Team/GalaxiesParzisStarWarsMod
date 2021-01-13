package com.parzivail.pswg.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class LightsaberRenderer
{
	private static final RenderLayer LAYER_LIGHTSABER_CORE = RenderLayer.of("lightsaber_core", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().build(true));

	private static final Function<Integer, Integer> BLEND_LIGHTSABER = (layer) -> (int)(1.675f * layer);

	private static final RenderLayer LAYER_LIGHTSABER_GLOW_THIRDPERSON = RenderLayer.of("lightsaber_glow_3p", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_transparency_3p", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ZERO);
		RenderSystem.enableCull();
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).target(new RenderPhase.Target("item_entity_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter())
			Client.minecraft.worldRenderer.getEntityFramebuffer().beginWrite(false);
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter())
			Client.minecraft.getFramebuffer().beginWrite(false);
	})).cull(new RenderPhase.Cull(true)).build(true));

	private static final RenderLayer LAYER_LIGHTSABER_GLOW_FIRSTPERSON = RenderLayer.of("lightsaber_glow_1p", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_transparency_1p", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ZERO);
		RenderSystem.enableCull();
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).build(true));

	private static final Function<Integer, Integer> BLEND_LIGHTSABER_DARK = (layer) -> (int)MathHelper.lerp((layer - 11) / 8f, 0, 192);

	private static final RenderLayer LAYER_LIGHTSABER_GLOW_DARK_THIRDPERSON = RenderLayer.of("lightsaber_glow_dark_3p", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, false, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_dark_transparency_3p", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ZERO);
		RenderSystem.enableCull();
		GL11.glPushAttrib(GL11.GL_POLYGON_BIT);
		GL11.glCullFace(GL11.GL_FRONT);
	}, () -> {
		GL11.glPopAttrib();
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).target(new RenderPhase.Target("item_entity_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter())
			Client.minecraft.worldRenderer.getEntityFramebuffer().beginWrite(false);
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter())
			Client.minecraft.getFramebuffer().beginWrite(false);
	})).cull(new RenderPhase.Cull(true)).build(true));

	private static final RenderLayer LAYER_LIGHTSABER_GLOW_DARK_FIRSTPERSON = RenderLayer.of("lightsaber_glow_dark_1p", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(new RenderPhase.Transparency("lightsaber_glow_dark_transparency_1p", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ZERO);
		RenderSystem.enableCull();
		GL11.glPushAttrib(GL11.GL_POLYGON_BIT);
		GL11.glCullFace(GL11.GL_FRONT);
	}, () -> {
		GL11.glPopAttrib();
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	})).cull(new RenderPhase.Cull(true)).build(true));

	public static void renderBlade(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean unstable, float baseLength, float lengthCoefficient, boolean cap, int coreColor, int glowColor, boolean darkBlend)
	{
		VertexConsumer vc;

		float totalLength = baseLength * lengthCoefficient;
		float shake = (1.1f - lengthCoefficient) * 0.004f;

		double dX = (float)Resources.RANDOM.nextGaussian() * shake;
		double dY = (float)Resources.RANDOM.nextGaussian() * shake;
		matrices.translate(dX, 0, dY);

		vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER_CORE);
		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderCore(totalLength, coreColor, unstable, cap);

		if (renderMode != ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND && renderMode != ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND)
			vc = vertexConsumers.getBuffer(darkBlend ? LAYER_LIGHTSABER_GLOW_DARK_THIRDPERSON : LAYER_LIGHTSABER_GLOW_THIRDPERSON);
		else
			vc = vertexConsumers.getBuffer(darkBlend ? LAYER_LIGHTSABER_GLOW_DARK_FIRSTPERSON : LAYER_LIGHTSABER_GLOW_FIRSTPERSON);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderGlow(totalLength, glowColor, unstable, cap, darkBlend);
	}

	private static void renderCore(float bladeLength, int coreColor, boolean unstable, boolean cap)
	{
		if (bladeLength == 0)
			return;

		int segments = unstable ? 15 : 1;
		float dSegments = 1f / segments;
		float dLength = bladeLength / segments;
		float offset = (float)Resources.RANDOM.nextGaussian();

		VertexConsumerBuffer.Instance.setColor(coreColor);

		if (cap)
		{
			float dTRoundBottom = unstable ? (float)Resources.SIMPLEX_0.noise2(offset, dLength * (segments + 1)) * 0.005f : 0;
			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, 0.01f, 0.022f + dTRoundBottom, 0, bladeLength + 0.02f, 0, 0, bladeLength, 0);
		}

		for (int i = 0; i < segments; i++)
		{
			float topThicknessLerp = MathHelper.lerp(dSegments * (i + 1), 0.035f, cap ? 0.022f : 0.035f);
			float bottomThicknessLerp = MathHelper.lerp(dSegments * i, 0.035f, cap ? 0.022f : 0.035f);

			float dTTop = unstable ? (float)Resources.SIMPLEX_0.noise2(offset, dLength * (i + 1)) * 0.005f : 0;
			float dTBottom = unstable ? (float)Resources.SIMPLEX_0.noise2(offset, dLength * i) * 0.005f : 0;

			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
		}
	}

	public static void renderGlow(float bladeLength, int bladeColor, boolean unstable, boolean cap, boolean darkBlend)
	{
		if (bladeLength == 0)
			return;

		float thicknessTop = cap ? 0.14f : 0.16f;

		for (int layer = 19; layer > 10; layer--)
		{
			VertexConsumerBuffer.Instance.setColor(bladeColor, (darkBlend ? BLEND_LIGHTSABER_DARK : BLEND_LIGHTSABER).apply(layer));

			float layerThicknessModifier = 0;
			if (unstable)
				layerThicknessModifier = (float)Resources.RANDOM.nextGaussian() * 0.003f;

			RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, thicknessTop - layerThicknessModifier - 0.0058f * layer, 0.16f - layerThicknessModifier - 0.0058f * layer, 0, cap ? layerThicknessModifier + bladeLength - 0.33f + 0.4f * (float)Math.sqrt(1 - Math.pow(1 - layer / 19f, 2)) : bladeLength + (20 - layer) * 0.005f, 0, 0, -(20 - layer) * 0.005f, 0);
		}
	}
}
