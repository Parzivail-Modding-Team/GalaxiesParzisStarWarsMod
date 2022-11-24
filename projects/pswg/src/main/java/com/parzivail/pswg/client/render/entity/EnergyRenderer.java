package com.parzivail.pswg.client.render.entity;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.mixin.RenderPhaseAccessor;
import com.parzivail.util.client.RenderShapes;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.math.ColorUtil;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EnergyRenderer
{
	public static final RenderLayer LAYER_ENERGY = RenderLayer.of("pswg:energy", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(RenderPhaseAccessor.get_LIGHTNING_SHADER()).transparency(RenderPhaseAccessor.get_TRANSLUCENT_TRANSPARENCY()).layering(RenderPhaseAccessor.get_VIEW_OFFSET_Z_LAYERING()).build(true));
	private static final RenderLayer LAYER_ENERGY_ADDITIVE = RenderLayer.of("pswg:energy_add", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().shader(RenderPhaseAccessor.get_LIGHTNING_SHADER()).transparency(RenderPhaseAccessor.get_LIGHTNING_TRANSPARENCY()).layering(RenderPhaseAccessor.get_VIEW_OFFSET_Z_LAYERING()).build(true));

	public static void renderDarksaber(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float baseLength, float lengthCoefficient)
	{
		VertexConsumer vc;

		var totalLength = baseLength * lengthCoefficient;
		var shake = (1.1f - lengthCoefficient) * 0.004f;

		double dX = (float)Resources.RANDOM.nextGaussian() * shake;
		double dY = (float)Resources.RANDOM.nextGaussian() * shake;
		matrices.translate(dX, 0, dY);

		vc = vertexConsumers.getBuffer(LAYER_ENERGY);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderDarksaberGlow(totalLength);
	}

	public static void renderEnergy(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean unstable, float baseLength, float lengthCoefficient, boolean cap, float glowHue, float glowSat, float glowVal)
	{
		VertexConsumer vc;

		var totalLength = baseLength * lengthCoefficient;
		var shake = (1.1f - lengthCoefficient) * 0.004f;

		if (unstable)
			shake *= 2;

		double dX = (float)Resources.RANDOM.nextGaussian() * shake;
		double dY = (float)Resources.RANDOM.nextGaussian() * shake;
		matrices.translate(dX, 0, dY);

		vc = vertexConsumers.getBuffer(LAYER_ENERGY);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
		renderGlow(totalLength, glowHue, glowSat, glowVal, unstable, cap);
	}

	public static void renderStunEnergy(ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float size, Vec3d normal, float glowHue)
	{
		VertexConsumer vc;

		vc = vertexConsumers.getBuffer(LAYER_ENERGY_ADDITIVE);

		VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 0.1f, 0.2f, 1, 1.0f, overlay, light);

		size /= 2;
		var nx = 0f;
		var ny = 0f;
		var nz = 1f;
		var d = 0.8f * size;

		// front cull
		VertexConsumerBuffer.Instance.vertex(-size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, d, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, -size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, d, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(-size, -size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, -size, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(-size, -size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, -d, 0, nx, ny, nz, 0, 0);

		// back cull
		VertexConsumerBuffer.Instance.vertex(-d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-size, size, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, -size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(size, size, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(size, -size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-size, -size, 0, nx, ny, nz, 0, 0);

		VertexConsumerBuffer.Instance.vertex(-d, -d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-d, d, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-size, size, 0, nx, ny, nz, 0, 0);
		VertexConsumerBuffer.Instance.vertex(-size, -size, 0, nx, ny, nz, 0, 0);
	}

	private static float getAlpha(double layer)
	{
		return (float)MathHelper.clamp(1 - (layer / 100 + 0.4) / (1 + Math.exp(-0.3 * (layer - 22))), 0, 1);
	}

	private static float getSaturation(double layer, double target)
	{
		var layeredSat = MathHelper.clamp((layer / 400 + 0.76) / (1 + Math.exp(-0.27 * (layer - 10))), 0, 1);
		return (float)(layeredSat * target);
	}

	private static float getValue(double layer, double target)
	{
		return (float)MathHelper.lerp(Ease.outCubic((float)(layer / 75)), 1, target);
	}

	private static float getHue(double h, double x)
	{
		return (float)MathHelper.clamp(-0.06 * Math.exp(-0.011 * Math.pow(x - 6, 2)) + h, 0, 1);
	}

	public static void renderGlow(float bladeLength, float glowHue, float glowSat, float glowVal, boolean unstable, boolean cap)
	{
		if (bladeLength == 0)
			return;

		var thicknessBottom = 0.018f;
		var thicknessTop = cap ? 0.012f : thicknessBottom;

		var mL = 0;
		var xL = 14;

		var deltaThickness = 0.0028f;

		var minOutputLayer = mL * thicknessBottom / deltaThickness;

		var globalTime = ((System.currentTimeMillis()) % Integer.MAX_VALUE) / 4f;

		for (var layer = mL; layer <= xL; layer++)
		{
			var time = ((System.currentTimeMillis() - layer * 10) % Integer.MAX_VALUE) / 200f;
			var noise = (float)Resources.SIMPLEX_0.noise2(0, time);

			var hueOffset = unstable ? (noise * 0.02f) : 0;

			var x = MathUtil.remap(layer, mL, xL, minOutputLayer, 60);
			var alpha = getAlpha(x);
			if (alpha < 16 / 255f)
				continue;

			var color = ColorUtil.hsvToRgbInt(
					getHue(glowHue + hueOffset, x),
					getSaturation(x, glowSat),
					getValue(x, glowVal)
			);
			VertexConsumerBuffer.Instance.setColor(color, (int)(255 * alpha));
			var layerThickness = deltaThickness * layer;

			if (layer > 0)
			{
				// glow layers
				RenderShapes.invertCull(true);
				RenderShapes.drawSolidBoxSkewTaper(
						VertexConsumerBuffer.Instance,
						thicknessTop + layerThickness,
						thicknessBottom + layerThickness,
						0, bladeLength + layerThickness, 0,
						0, -layerThickness, 0
				);
				RenderShapes.invertCull(false);
			}
			else
			{
				// core layer
				final var segments = unstable ? 35 : 1;
				final var dSegments = 1f / segments;
				final var dLength = bladeLength / segments;

				final var dLengthTime = 5;

				for (var i = 0; i < segments; i++)
				{
					var topThicknessLerp = MathHelper.lerp(dSegments * (i + 1), thicknessBottom, thicknessTop);
					var bottomThicknessLerp = MathHelper.lerp(dSegments * i, thicknessBottom, thicknessTop);

					var dTTop = unstable ? (float)Resources.SIMPLEX_0.noise2(globalTime, dLengthTime * dLength * (i + 1)) * 0.005f : 0;
					var dTBottom = unstable ? (float)Resources.SIMPLEX_0.noise2(globalTime, dLengthTime * dLength * i) * 0.005f : 0;

					noise = (float)Resources.SIMPLEX_0.noise2(globalTime, 3 * dLength * i);
					color = ColorUtil.hsvToRgbInt(
							0,
							(unstable ? (0.07f - noise * 0.07f) : 0) * glowSat,
							getValue(x, glowVal)
					);
					VertexConsumerBuffer.Instance.setColor(color, (int)(255 * getAlpha(x)));

					RenderShapes.drawSolidBoxSkewTaper(VertexConsumerBuffer.Instance, topThicknessLerp + dTTop, bottomThicknessLerp + dTBottom, 0, dLength * (i + 1), 0, 0, dLength * i, 0);
				}
			}
		}
	}

	public static void renderDarksaberGlow(float bladeLength)
	{
		if (bladeLength == 0)
			return;

		var thicknessBottom = 0.02f;
		var thicknessTop = 0.018f;

		var mL = 0;
		var xL = 14;

		var deltaThickness = 0.0028f;

		var minOutputLayer = mL * thicknessBottom / deltaThickness;

		for (var layer = mL; layer <= xL; layer++)
		{
			var x = MathUtil.remap(layer, mL, xL, minOutputLayer, 70);
			var alpha = getAlpha(x);
			if (alpha < 16 / 255f)
				continue;

			var layerThickness = deltaThickness * layer;

			if (layer > 0)
			{
				VertexConsumerBuffer.Instance.setColor(0xFFFFFF, (int)(255 * alpha));

				// glow layers
				RenderShapes.invertCull(true);
				RenderShapes.skipFace(1);
				RenderShapes.drawSolidBoxSkewTaper(
						VertexConsumerBuffer.Instance,
						thicknessTop + layerThickness,
						thicknessBottom + layerThickness,
						0, bladeLength * 0.6f + layerThickness, 0,
						0, -layerThickness, 0
				);
				RenderShapes.skipFace(3);
				RenderShapes.drawSolidBoxSkewTaper(
						VertexConsumerBuffer.Instance,
						thicknessTop / 4f + layerThickness,
						thicknessTop + layerThickness,
						0, bladeLength + layerThickness, thicknessTop * 0.75f,
						0, bladeLength * 0.6f + layerThickness, 0
				);
				RenderShapes.skipFace(-1);
				RenderShapes.invertCull(false);
			}
			else
			{
				VertexConsumerBuffer.Instance.setColor(0x101010, (int)(255 * getAlpha(x)));
				RenderShapes.drawSolidBoxSkewTaper(
						VertexConsumerBuffer.Instance,
						thicknessTop,
						thicknessBottom,
						0, bladeLength * 0.6f, 0,
						0, 0, 0
				);
				RenderShapes.drawSolidBoxSkewTaper(
						VertexConsumerBuffer.Instance,
						thicknessTop / 4f,
						thicknessTop,
						0, bladeLength, thicknessTop * 0.75f,
						0, bladeLength * 0.6f, 0
				);
			}
		}
	}

	public static void renderLayer(BufferBuilderStorage bufferBuilders)
	{
		bufferBuilders.getEntityVertexConsumers().draw(EnergyRenderer.LAYER_ENERGY);
	}
}
