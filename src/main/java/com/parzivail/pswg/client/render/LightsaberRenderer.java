package com.parzivail.pswg.client.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.mixin.RenderPhaseAccessor;
import com.parzivail.util.client.ColorUtil;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class LightsaberRenderer
{
	private static final RenderLayer LAYER_LIGHTSABER = RenderLayer.of("lightsaber_core", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(RenderPhaseAccessor.get_LIGHTNING_SHADER()).transparency(RenderPhaseAccessor.get_TRANSLUCENT_TRANSPARENCY()).layering(RenderPhaseAccessor.get_VIEW_OFFSET_Z_LAYERING()).build(true));

	public static void renderBlade(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean unstable, float baseLength, float lengthCoefficient, boolean cap, int coreColor, int glowColor)
	{
		VertexConsumer vc;

		var totalLength = baseLength * lengthCoefficient;
		var shake = (1.1f - lengthCoefficient) * 0.004f;

		double dX = (float)Resources.RANDOM.nextGaussian() * shake;
		double dY = (float)Resources.RANDOM.nextGaussian() * shake;
		matrices.translate(dX, 0, dY);

		vc = vertexConsumers.getBuffer(LAYER_LIGHTSABER);

		final var offset = (float)Resources.RANDOM.nextGaussian();

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		//		renderCore(totalLength, coreColor | 0xFF000000, unstable, offset, cap);
		RenderShapes.invertCull(() -> renderGlow(totalLength, glowColor, unstable, cap));
	}

	private static float getAlpha(double x)
	{
		return (float)MathHelper.clamp(1 - (x / 100 + 0.4) / (1 + Math.exp(-0.3 * (x - 22))), 0, 1);
	}

	private static float getSaturation(double x)
	{
		return (float)MathHelper.clamp((x / 400 + 0.76) / (1 + Math.exp(-0.27 * (x - 10))), 0, 1);
	}

	private static float getHue(double h, double x)
	{
		return (float)MathHelper.clamp(-0.06 * Math.exp(-0.011 * Math.pow(x - 6, 2)) + h, 0, 1);
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

		var thicknessTop = cap ? 0.01f : 0.01f;

		for (var layer = 0; layer <= 12; layer++)
		{
			// TODO: hue
			var x = MathUtil.remap(layer, 0, 12, 5, 60);
			int color = ColorUtil.fromHSV(
					getHue(0.61f, x),
					getSaturation(x),
					1
			);
			VertexConsumerBuffer.Instance.setColor(color, (int)(255 * getAlpha(x)));

			float layerThicknessModifier = 0;
			if (unstable)
				layerThicknessModifier = (float)Resources.RANDOM.nextGaussian() * 0.003f;

			float dT = 0.0035f * layer;

			RenderShapes.drawSolidBoxSkewTaper(
					VertexConsumerBuffer.Instance,
					thicknessTop - layerThicknessModifier + dT,
					0.015f - layerThicknessModifier + dT,
					0, layerThicknessModifier + bladeLength + dT, 0,
					0, -dT, 0
			);
		}
	}
}
